package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wms.mapper.UserMapper;
import com.wms.pojo.User;
import com.wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("password", password);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public List<User> listUsers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        return userMapper.selectList(wrapper);
    }
}

