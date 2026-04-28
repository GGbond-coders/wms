package com.wms.service;

import com.wms.pojo.User;

public interface UserService {
    User login(String username, String password);
}