package com.campus.trade.service;

import com.campus.trade.pojo.User;
import com.campus.trade.pojo.dto.AdminAddUserDTO;
import com.campus.trade.pojo.dto.LoginDTO;
import com.campus.trade.pojo.dto.RegisterDTO;

import java.util.List;

public interface UserService {

    String login(LoginDTO loginDTO);

    String adminLogin(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    User getById(Long id);

    void update(User user);
    
    void delete(Long id);

    List<User> search(String keyword);

    void adminAddUser(AdminAddUserDTO adminAddUserDTO);
    
    void resetPassword(String phone, String newPassword);
}