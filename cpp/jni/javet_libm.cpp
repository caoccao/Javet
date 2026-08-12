/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 /*
  * V8 and ICU reference a few dozen math symbols that clang's driver would
  * normally satisfy with an implicit -lm. Resolving them from the system libm
  * makes libjavet depend on libm.so.6 being in scope, which is not guaranteed
  * on every JVM, and glibc's libm.a is not an option either: it is built
  * without -fPIC, so linking it into a shared object fails outright on x86_64
  * (its ifunc resolvers and even the plain e_pow.o/e_exp2.o/e_log2.o reach
  * their data tables through PC32 relocations against global symbols).
  *
  * V8 already vendors llvm-libc under third_party/llvm-libc and routes
  * Math.pow through it, so this file supplies the same symbols from that
  * header-only implementation, compiled -fPIC into libjavet itself. Every
  * symbol here is marked local by jni/version_script.map, so these definitions
  * bind only within libjavet and never interpose on the host process.
  *
  * The set below is exactly what libjavet-v8-linux-* leaves undefined; the
  * i18n build needs all of them, the non-i18n build a subset. If a V8 or ICU
  * upgrade adds a new one, libm.so.6 quietly reappears in DT_NEEDED, so verify
  * with `readelf -d libjavet-v8-linux-*.so | grep NEEDED` after upgrading.
  *
  * frexp, frexpl, ldexp, modf and modff are carried by libc as well as libm,
  * so in principle libc could serve them. They are still defined here because
  * clang's driver puts -lm ahead of -lc: leaving them out lets the linker
  * satisfy them from libm and keep it in DT_NEEDED, which defeats the point.
  * expm1 is likewise not strictly required; tanh below is built on it.
  *
  * Only tanh and powl are hand-written: llvm-libc ships tanhf but no double
  * tanh, and no long double pow at all.
  */

#ifdef JAVET_STATIC_LIBM

 /*
  * llvm-libc's shared headers point its math implementations at the system
  * fenv: shared/libc_common.h defines LIBC_MATH_USE_SYSTEM_FENV, which is what
  * drags feclearexcept and feraiseexcept out of libm to begin with. Undefining
  * it before FEnvImpl.h is first seen selects llvm-libc's own MXCSR/FPCR
  * implementation instead, so the two definitions below do not tail-call
  * straight back into themselves. Both must precede every other llvm-libc
  * header, since they all reach FEnvImpl.h and it is include-guarded.
  */
#include "shared/libc_common.h"
#undef LIBC_MATH_USE_SYSTEM_FENV
#include "src/__support/FPUtil/FEnvImpl.h"

#include "shared/math/asin.h"
#include "shared/math/atan.h"
#include "shared/math/atan2.h"
#include "shared/math/exp.h"
#include "shared/math/exp2.h"
#include "shared/math/expf.h"
#include "shared/math/expm1.h"
#include "shared/math/frexp.h"
#include "shared/math/frexpl.h"
#include "shared/math/ldexp.h"
#include "shared/math/log.h"
#include "shared/math/log10.h"
#include "shared/math/log2.h"
#include "shared/math/modf.h"
#include "shared/math/modff.h"
#include "shared/math/nan.h"
#include "shared/math/nanf.h"
#include "shared/math/nearbyint.h"
#include "shared/math/nearbyintf.h"
#include "shared/math/nextafter.h"
#include "shared/math/nextafterf.h"
#include "shared/math/pow.h"
#include "shared/math/powf.h"
#include "shared/math/sin.h"
#include "shared/math/sincos.h"
#include "shared/math/tanhf.h"

 /*
  * shared/math/tan.h is deliberately not included. It defines
  * tan_internal::newton_raphson_div at namespace scope without LIBC_INLINE, so
  * every translation unit including it emits a strong definition, and V8's
  * src/base/ieee754.cc already includes it. Pulling it in here is a duplicate
  * symbol at link time. V8's own tan is that same llvm-libc implementation, so
  * forward to it rather than emitting a second copy.
  */
namespace v8 {
    namespace base {
        namespace ieee754 {
            double tan(double x);
        }
    }
}

namespace {
    // Subtracting this from one rounds back to one while raising inexact, which
    // is how fdlibm and glibc flag tanh(|x| >= 22).
    constexpr double kTanhTiny = 1.0e-300;
    // Below 2^-28 the fdlibm expansion of tanh degenerates to the identity.
    constexpr double kTanhIdentityThreshold = 0x1p-28;
    // tanh saturates to +-1 in double precision beyond this point.
    constexpr double kTanhSaturationThreshold = 22.0;
    // Bound on the exponent handled by powl's exact integral path. The only
    // caller is V8's Intl.DurationFormat, which asks for 10^n with n <= 9.
    constexpr long double kPowlMaxIntegralExponent = 1024.0L;
}  // namespace

extern "C" {

    double asin(double x) { return LIBC_NAMESPACE::shared::asin(x); }

    double atan(double x) { return LIBC_NAMESPACE::shared::atan(x); }

    /*
     * llvm-libc drops the sign of a zero result when y/x underflows, e.g.
     * atan2(-1e-300, 1e300) yields +0 where -0 is required. A zero result only
     * arises with x > 0, where the sign always follows y.
     */
    double atan2(double y, double x) {
        double result = LIBC_NAMESPACE::shared::atan2(y, x);
        return result == 0.0 ? __builtin_copysign(result, y) : result;
    }

    double exp(double x) { return LIBC_NAMESPACE::shared::exp(x); }

    double exp2(double x) { return LIBC_NAMESPACE::shared::exp2(x); }

    float expf(float x) { return LIBC_NAMESPACE::shared::expf(x); }

    double expm1(double x) { return LIBC_NAMESPACE::shared::expm1(x); }

    double frexp(double x, int* exp) { return LIBC_NAMESPACE::shared::frexp(x, exp); }

    long double frexpl(long double x, int* exp) { return LIBC_NAMESPACE::shared::frexpl(x, exp); }

    double ldexp(double x, int exp) { return LIBC_NAMESPACE::shared::ldexp(x, exp); }

    double log(double x) { return LIBC_NAMESPACE::shared::log(x); }

    double log10(double x) { return LIBC_NAMESPACE::shared::log10(x); }

    double log2(double x) { return LIBC_NAMESPACE::shared::log2(x); }

    double modf(double x, double* iptr) { return LIBC_NAMESPACE::shared::modf(x, iptr); }

    float modff(float x, float* iptr) { return LIBC_NAMESPACE::shared::modff(x, iptr); }

    double nan(const char* arg) { return LIBC_NAMESPACE::shared::nan(arg); }

    float nanf(const char* arg) { return LIBC_NAMESPACE::shared::nanf(arg); }

    double nearbyint(double x) { return LIBC_NAMESPACE::shared::nearbyint(x); }

    float nearbyintf(float x) { return LIBC_NAMESPACE::shared::nearbyintf(x); }

    double nextafter(double x, double y) { return LIBC_NAMESPACE::shared::nextafter(x, y); }

    float nextafterf(float x, float y) { return LIBC_NAMESPACE::shared::nextafterf(x, y); }

    /*
     * llvm-libc's pow mis-signs its overflow and underflow results once |y|
     * reaches 2^53, e.g. pow(22, 1e300) yields -inf where +inf is required.
     * At that magnitude y is necessarily an even integer, so the result is
     * decided entirely by |x| and is never negative; settle those directly and
     * leave everything below the threshold, including a NaN x, to llvm-libc.
     */
    double pow(double x, double y) {
        if (__builtin_fabs(y) >= 0x1p53 && !__builtin_isinf(y) && !__builtin_isnan(x)) {
            double base = __builtin_fabs(x);
            if (base == 1.0) {
                return 1.0;
            }
            return (base > 1.0) == (y > 0.0) ? __builtin_inf() : 0.0;
        }
        return LIBC_NAMESPACE::shared::pow(x, y);
    }

    float powf(float x, float y) { return LIBC_NAMESPACE::shared::powf(x, y); }

    /*
     * llvm-libc has no long double pow. V8 only calls it as pow(10.0L, n) with
     * a small non-negative integral n, so exponentiate by squaring whenever the
     * exponent is integral, which is exact for those inputs. Anything else,
     * including NaN and infinite exponents, falls through to double precision.
     */
    long double powl(long double x, long double y) {
        if (y >= -kPowlMaxIntegralExponent && y <= kPowlMaxIntegralExponent) {
            long long exponent = static_cast<long long>(y);
            if (static_cast<long double>(exponent) == y) {
                unsigned long long magnitude = exponent < 0
                    ? 0ULL - static_cast<unsigned long long>(exponent)
                    : static_cast<unsigned long long>(exponent);
                long double result = 1.0L;
                long double base = x;
                while (magnitude != 0ULL) {
                    if ((magnitude & 1ULL) != 0ULL) {
                        result *= base;
                    }
                    magnitude >>= 1;
                    if (magnitude != 0ULL) {
                        base *= base;
                    }
                }
                return exponent < 0 ? 1.0L / result : result;
            }
        }
        return static_cast<long double>(
            LIBC_NAMESPACE::shared::pow(static_cast<double>(x), static_cast<double>(y)));
    }

    double sin(double x) { return LIBC_NAMESPACE::shared::sin(x); }

    void sincos(double x, double* sinx, double* cosx) {
        LIBC_NAMESPACE::shared::sincos(x, sinx, cosx);
    }

    double tan(double x) { return v8::base::ieee754::tan(x); }

    /*
     * llvm-libc has no double tanh, so this is fdlibm's expm1 formulation, the
     * same one glibc and V8's own ieee754.cc lineage use.
     */
    double tanh(double x) {
        if (__builtin_isnan(x)) {
            return x + x;
        }
        double magnitude = __builtin_fabs(x);
        double result;
        if (magnitude < kTanhIdentityThreshold) {
            return x;
        } else if (__builtin_isinf(magnitude) || magnitude >= kTanhSaturationThreshold) {
            result = 1.0 - kTanhTiny;
        } else if (magnitude >= 1.0) {
            double t = LIBC_NAMESPACE::shared::expm1(2.0 * magnitude);
            result = 1.0 - 2.0 / (t + 2.0);
        } else {
            double t = LIBC_NAMESPACE::shared::expm1(-2.0 * magnitude);
            result = -t / (t + 2.0);
        }
        return __builtin_signbit(x) ? -result : result;
    }

    float tanhf(float x) { return LIBC_NAMESPACE::shared::tanhf(x); }

    /*
     * glibc's <fenv.h> already carries __extern_inline bodies for these two, so
     * defining them by name is a redefinition error, and hanging an __asm__
     * label off a declaration silently yields a zero-sized symbol that falls
     * through into whatever code follows it. Define them under private names
     * and publish the public ones as assembler aliases instead.
     */
    int javet_feclearexcept(int excepts) { return LIBC_NAMESPACE::fputil::clear_except(excepts); }

    int javet_feraiseexcept(int excepts) { return LIBC_NAMESPACE::fputil::raise_except(excepts); }

}  // extern "C"

__asm__(
    ".globl feclearexcept\n"
    ".type feclearexcept, @function\n"
    ".set feclearexcept, javet_feclearexcept\n"
    ".globl feraiseexcept\n"
    ".type feraiseexcept, @function\n"
    ".set feraiseexcept, javet_feraiseexcept\n");

#endif  // JAVET_STATIC_LIBM
