package com.wms.controller;

import com.wms.common.JwtUtils;
import com.wms.pojo.User;
import com.wms.service.UserService;
import com.wms.vo.ResultVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResultVO<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        if (user != null) {
            // 生成JWT token
            String token = JwtUtils.generateToken(user);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            return ResultVO.success(data);
        } else {
            return ResultVO.error("用户名或密码错误");
        }
    }

    @GetMapping("/user/info")
    public ResultVO<User> getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        return ResultVO.success(user);
    }
}