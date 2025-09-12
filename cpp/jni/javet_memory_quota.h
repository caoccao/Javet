#pragma once
#include <atomic>
#include <chrono>
#include "v8.h"

namespace javet {
  // Holds memory accounting data for a V8 isolate.  Counts both external
  // allocations (ArrayBuffer/TypedArray backing stores and WASM memories)
  // and sampled heap usage.  When hard_limit > 0 and total() exceeds it,
  // the custom allocator will deny further allocations.
  struct IsolateMemoryQuota {
    std::atomic<size_t> external_bytes{0};
    std::atomic<size_t> last_heap_used{0};
    size_t hard_limit{0};
    size_t soft_limit{0};
    size_t sample_step_bytes{1024 * 1024};
    uint64_t sample_cadence_ns{5'000'000};
    std::atomic<size_t> last_sampled_external{0};
    std::atomic<uint64_t> last_sampled_ns{0};

    v8::Isolate* isolate{nullptr};

    inline size_t total() const noexcept {
      return external_bytes.load(std::memory_order_relaxed) +
             last_heap_used.load(std::memory_order_relaxed);
    }
  };

  class QuotaArrayBufferAllocator final : public v8::ArrayBuffer::Allocator {
  public:
    explicit QuotaArrayBufferAllocator(IsolateMemoryQuota* quota) : quota_(quota) {}
    void* Allocate(size_t length) override { return AllocateImpl(length, /*zero_init=*/true); }
    void* AllocateUninitialized(size_t length) override { return AllocateImpl(length, /*zero_init=*/false); }
    void Free(void* data, size_t length) override {
      if (!data) return;
      std::free(data);
      quota_->external_bytes.fetch_sub(length, std::memory_order_relaxed);
      quota_->isolate->AdjustAmountOfExternalAllocatedMemory(-(static_cast<int64_t>(length)));
    }
    void* Reallocate(void* data, size_t old_len, size_t new_len) override {
      // Simple reallocate: allocate a new block, copy old contents, free old block.
      void* p = AllocateImpl(new_len, /*zero_init=*/true);
      if (!p) return nullptr;
      if (data && old_len) std::memcpy(p, data, std::min(old_len, new_len));
      Free(data, old_len);
      return p;
    }

  private:
    inline uint64_t nowNs() const noexcept {
      using namespace std::chrono;
      return duration_cast<nanoseconds>(steady_clock::now().time_since_epoch()).count();
    }

    inline bool shouldResample(size_t pendingAlloc) {
      if (quota_->hard_limit == 0) return false;  // no limit, no resample needed

      const size_t ext = quota_->external_bytes.load(std::memory_order_relaxed);
      const size_t heap = quota_->last_heap_used.load(std::memory_order_relaxed);
      const size_t projected = ext + heap + pendingAlloc;

      // Fast path: if well below soft limit, skip resample.
      if (quota_->soft_limit && projected <= quota_->soft_limit) return false;

      // Near/above soft limit => consider resample, but apply step/time gates to avoid thrash.
      const size_t lastExt = quota_->last_sampled_external.load(std::memory_order_relaxed);
      const uint64_t lastNs = quota_->last_sampled_ns.load(std::memory_order_relaxed);
      const size_t grown = (ext >= lastExt) ? (ext - lastExt) : 0;
      const uint64_t age = (lastNs == 0) ? UINT64_MAX : (nowNs() - lastNs);

      if (grown >= quota_->sample_step_bytes) return true;
      if (age >= quota_->sample_cadence_ns)   return true;
      // If we’re over hard limit *without* fresh stats, force resample.
      if (quota_->hard_limit && projected > quota_->hard_limit) return true;
      return true; // We crossed soft limit; resample once to be safe.
    }

    inline void refreshHeapSample() {
      v8::HeapStatistics hs;
      quota_->isolate->GetHeapStatistics(&hs);
      quota_->last_heap_used.store(hs.used_heap_size(), std::memory_order_relaxed);
      quota_->last_sampled_external.store(quota_->external_bytes.load(std::memory_order_relaxed),
                                          std::memory_order_relaxed);
      quota_->last_sampled_ns.store(nowNs(), std::memory_order_relaxed);
    }


    void* AllocateImpl(size_t length, bool zero_init) {
      if (length == 0) return nullptr;
      if (quota_->hard_limit) {
        // Only resample when near the cap or step/time thresholds are met.
        if (shouldResample(length)) {
          refreshHeapSample();
          size_t projected = quota_->total() + length;
          if (projected > quota_->hard_limit) {
            // Try once to recover before denying.
            quota_->isolate->LowMemoryNotification();
            refreshHeapSample();
            projected = quota_->total() + length;
            if (projected > quota_->hard_limit) {
              return nullptr; // deny; V8 will surface OOM
            }
          }
        }
      }

      void* p = std::malloc(length);
      if (!p) return nullptr;
      if (zero_init) std::memset(p, 0, length);
      quota_->external_bytes.fetch_add(length, std::memory_order_relaxed);
      quota_->isolate->AdjustAmountOfExternalAllocatedMemory(static_cast<int64_t>(length));
      return p;
    }
    IsolateMemoryQuota* quota_;
  };

  // Data slot used to store a pointer to the IsolateMemoryQuota in the isolate.
  constexpr int kJavetQuotaDataSlot = 1;

  inline IsolateMemoryQuota* GetQuota(v8::Isolate* isolate) {
    return static_cast<IsolateMemoryQuota*>(isolate->GetData(kJavetQuotaDataSlot));
  }
}

