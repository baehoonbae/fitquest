package com.web.fitquest.service.user;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${upload.path}")
    private String uploadPath;

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
    public Optional<User> selectUserById(Integer id) {
        return Optional.ofNullable(userMapper.selectUserById(id));
    }

    @Override
    public String updateProfileImage(Integer userId, MultipartFile file) throws IOException {
        // 파일명 고유화
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // 업로드 디렉토리 생성
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 파일 저장 경로 생성
        String filePath = uploadPath + "/" + uniqueFileName;
        File savedFile = new File(filePath);

        // 파일 저장
        file.transferTo(savedFile);

        // 데이터베이스에 저장할 상대 경로
        String dbPath = "/uploads/" + uniqueFileName;
        
        // 사용자 정보 업데이트
        User user = userMapper.selectUserById(userId);
        user.setProfileImage(dbPath);
        userMapper.updateUser(user);

        return dbPath;
    }
}
