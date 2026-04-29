package com.wms.service;

import com.wms.pojo.User;

import java.util.List;

public interface UserService {
    User login(String username, String password);

    List<User> listUsers();
}
