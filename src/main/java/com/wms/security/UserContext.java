package com.wms.security;

import com.wms.pojo.User;

public class UserContext {
    private static final ThreadLocal<User> CURRENT = new ThreadLocal<>();

    public static void set(User user) {
        CURRENT.set(user);
    }

    public static User get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}

