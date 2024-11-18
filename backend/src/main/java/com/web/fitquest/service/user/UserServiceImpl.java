package com.web.fitquest.service.user;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.web.fitquest.mapper.user.UserMapper;
import com.web.fitquest.model.user.User;
import com.web.fitquest.requests.LoginRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> login(LoginRequest loginRequest) {
        User user = userMapper.selectUserByEmail(loginRequest.getEmail());
        if(user!=null){
            log.info("user: {}", user);
            if(user.getPassword().equals(loginRequest.getPassword())){
                String encodedPassword = passwordEncoder.encode(loginRequest.getPassword());
                user.setPassword(encodedPassword);
                userMapper.updateUser(user);
                return Optional.of(user);
            }else if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean regist(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public Optional<User> selectUserByName(String name) {
        return Optional.ofNullable(userMapper.selectUserByName(name));
    }

    @Override
    public Optional<User> selectUserByEmail(String email) {
        return Optional.ofNullable(userMapper.selectUserByEmail(email));
    }

    @Override
    public Optional<User> getUserInfo(Integer id) {
        return Optional.ofNullable(userMapper.selectUserById(id));
    }
}
