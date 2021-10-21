package java.lang.management;

public interface MemoryPoolMXBean {
    boolean isUsageThresholdSupported();

    default MemoryType getType() {
        return null;
    }

    MemoryUsage getUsage();

    void setUsageThreshold(long memoryUsageThreshold);
}
