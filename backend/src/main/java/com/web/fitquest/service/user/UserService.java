package com.web.fitquest.service.user;

import java.util.Optional;

import com.web.fitquest.model.user.User;
import com.web.fitquest.requests.LoginRequest;

public interface UserService {
    Optional<User> login(LoginRequest loginRequest);
    boolean regist(User user);
    boolean updateUser(User user);
    Optional<User> selectUserByName(String name);
    Optional<User> selectUserByEmail(String email);
    Optional<User> getUserInfo(Integer id);
}
