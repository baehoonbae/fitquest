package com.web.fitquest.service.user;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.web.fitquest.mapper.user.UserMapper;
import com.web.fitquest.model.user.User;
import com.web.fitquest.requests.LoginRequest;
import com.web.fitquest.service.board.BoardService;

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

    @Autowired
    private BoardService boardService;

    @Override
    public Optional<User> login(LoginRequest loginRequest) {
        User user = userMapper.selectUserByEmail(loginRequest.getEmail());
        if (user != null) {
            log.info("user: {}", user);
            if (user.getPassword().equals(loginRequest.getPassword())) {
                String encodedPassword = passwordEncoder.encode(loginRequest.getPassword());
                user.setPassword(encodedPassword);
                userMapper.updateUser(user);
                return Optional.of(user);
            } else if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
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
    @Transactional
    public boolean updateUser(User user) {
        try {
            String[] parts = user.getName().split(",");
            String name = parts[0];
            String choseong = parts[1];
            user.setName(name);
            boardService.updateWriterChoseongByUserId(user.getId(), choseong);
            return userMapper.updateUser(user) > 0;
        } catch (Exception e) {
            log.error("사용자 정보 수정 실패", e);
            throw e;
        }
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
        // 기존 이미지 삭제 로직 추가
        User user = userMapper.selectUserById(userId);
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            // DB에 저장된 경로에서 파일명만 추출
            String oldFileName = user.getProfileImage().substring(user.getProfileImage().lastIndexOf("/") + 1);
            File oldFile = new File(uploadPath + "/profiles/" + oldFileName);
            if (oldFile.exists()) {
                if (oldFile.delete()) {
                    log.info("이전 프로필 이미지 삭제 성공: {}", oldFile.getAbsolutePath());
                } else {
                    log.warn("이전 프로필 이미지 삭제 실패: {}", oldFile.getAbsolutePath());
                }
            }
        }

        String uniqueFileName = UUID.randomUUID().toString() + getExtension(file.getOriginalFilename());

        // profiles 디렉토리 생성
        File uploadDir = new File(uploadPath + "/profiles");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 파일 저장 (uploads/profiles 폴더 아래에)
        String filePath = uploadPath + "/profiles/" + uniqueFileName;
        File savedFile = new File(filePath);

        log.info("파일 저장 경로: {}", filePath); // 로그 추가

        file.transferTo(savedFile);

        // DB에 저장할 경로 (/uploads/profiles/파일명 형식)
        String dbPath = "/uploads/profiles/" + uniqueFileName;
        user.setProfileImage(dbPath);
        userMapper.updateUser(user);

        return dbPath;
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return filename.substring(dotIndex);
    }
}
