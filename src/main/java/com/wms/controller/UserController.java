package com.wms.controller;

import com.wms.common.JwtUtils;
import com.wms.pojo.User;
import com.wms.service.UserService;
import com.wms.vo.ResultVO;
import com.wms.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResultVO<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        if (user == null) {
            return ResultVO.error("用户名或密码错误");
        }

        String token = JwtUtils.generateToken(user);
        UserInfoVO userInfo = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfo);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userInfo);
        return ResultVO.success(data);
    }

    @GetMapping("/user/info")
    public ResultVO<UserInfoVO> getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            return ResultVO.success(null);
        }
        UserInfoVO userInfo = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfo);
        return ResultVO.success(userInfo);
    }

    @GetMapping("/users")
    public ResultVO<List<UserInfoVO>> listUsers() {
        List<User> users = userService.listUsers();
        List<UserInfoVO> data = users.stream().map(u -> {
            UserInfoVO vo = new UserInfoVO();
            BeanUtils.copyProperties(u, vo);
            return vo;
        }).collect(Collectors.toList());
        return ResultVO.success(data);
    }
}

