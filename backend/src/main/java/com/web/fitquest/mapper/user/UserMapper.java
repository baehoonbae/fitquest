package com.web.fitquest.mapper.user;

import org.apache.ibatis.annotations.Mapper;

import com.web.fitquest.model.user.User;

@Mapper
public interface UserMapper {
    User selectUserByEmail(String email);
    User selectUserById(Integer id);
    User selectUserByName(String name);
    int insertUser(User user);
    int updateUser(User user);
    int updateProfileImage(Integer userId, String imageUrl);
}
