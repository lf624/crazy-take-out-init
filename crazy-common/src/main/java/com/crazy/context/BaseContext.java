package com.crazy.context;

public class BaseContext {
    public static ThreadLocal<Long> currentEmployeeId = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        currentEmployeeId.set(id);
    }

    public static Long getCurrentId() {
        return currentEmployeeId.get();
    }

    public static void removeCurrentId() {
        currentEmployeeId.remove();
    }
}
