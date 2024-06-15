package net.security.data.microservicesocr.Utils;

public class GlobalFlags {
    public static ThreadLocal<Boolean> myFlag = ThreadLocal.withInitial(() -> false);
}