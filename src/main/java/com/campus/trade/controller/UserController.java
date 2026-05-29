package com.campus.trade.controller;

import com.campus.trade.mapper.UserMapper;
import com.campus.trade.pojo.Result;
import com.campus.trade.pojo.User;
import com.campus.trade.pojo.dto.AdminAddUserDTO;
import com.campus.trade.pojo.dto.LoginDTO;
import com.campus.trade.pojo.dto.RegisterDTO;
import com.campus.trade.pojo.dto.ResetPasswordDTO;
import com.campus.trade.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userType", User.TYPE_USER);
        return Result.success(data);
    }

    @PostMapping("/admin/login")
    public Result<Map<String, Object>> adminLogin(@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.adminLogin(loginDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userType", User.TYPE_ADMIN);
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(registerDTO.getCaptcha())) {
            return Result.error("验证码错误");
        }
        session.removeAttribute("captcha");
        userService.register(registerDTO);
        return Result.success("注册成功");
    }

    @PostMapping("/reset-password")
    public Result<String> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(resetPasswordDTO.getCode())) {
            return Result.error("验证码错误");
        }
        session.removeAttribute("captcha");
        userService.resetPassword(resetPasswordDTO.getPhone(), resetPasswordDTO.getNewPassword());
        return Result.success("密码重置成功");
    }

    @GetMapping("/info")
    public Result<User> getInfo(@RequestAttribute("userId") Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody User user, @RequestAttribute("userId") Long userId) {
        user.setId(userId);
        userService.update(user);
        return Result.success("更新成功");
    }

    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(required = false) String keyword) {
        List<User> users;
        if (keyword != null && !keyword.isEmpty()) {
            users = userService.search(keyword);
        } else {
            users = userService.search(""); // 使用search方法来过滤已删除用户
        }
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }

    @PutMapping("/{id}")
    public Result<String> updateAdmin(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.update(user);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/admin/add")
    public Result<String> adminAddUser(@Valid @RequestBody AdminAddUserDTO adminAddUserDTO) {
        userService.adminAddUser(adminAddUserDTO);
        return Result.success("用户添加成功");
    }
}
