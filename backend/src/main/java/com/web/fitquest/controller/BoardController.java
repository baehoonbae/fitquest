package com.web.fitquest.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.web.fitquest.model.SearchHistory;
import com.web.fitquest.model.board.Board;
import com.web.fitquest.model.searchCondition.SearchCondition;
import com.web.fitquest.service.board.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    @Value("${upload.path}")
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<?> getAllBoards(@ModelAttribute SearchCondition searchCondition) {
        log.debug("BoardController/getAllBoards");
        try {
            Optional<List<Board>> result = boardService.allBoards(searchCondition);
            if (result.isPresent() && !result.get().isEmpty()) {
                return new ResponseEntity<List<Board>>(result.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return exceptionHandling(e);
        }
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable int id) {
        log.debug("BoardController/getBoard");
        try {
            Optional<Board> result = boardService.getBoard(id);
            if (result.isPresent()) {
                return new ResponseEntity<Board>(result.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return exceptionHandling(e);
        }
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<?> addBoard(@RequestBody Board board) {
        log.debug("BoardController/addBoard");
        try {
            Optional<Integer> result = boardService.addBoard(board);
            if (result.isPresent() && result.get() > 0) {
                return new ResponseEntity<Board>(board, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return exceptionHandling(e);
        }
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable int id, @RequestBody Board board) {
        log.debug("BoardController/updateBoard");
        try {
            board.setId(id);
            Optional<Integer> result = boardService.updateBoard(board);
            if (result.isPresent() && result.get() > 0) {
                return new ResponseEntity<Board>(board, HttpStatus.OK);
            } else {
                return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return exceptionHandling(e);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable int id) {
        log.debug("BoardController/deleteBoard");
        try {
            Optional<Integer> result = boardService.deleteBoard(id);
            if (result.isPresent() && result.get() > 0) {
                return new ResponseEntity<Integer>(result.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return exceptionHandling(e);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBoards(@RequestParam String searchText) {
        log.debug("BoardController/searchBoards: searchText = {}", searchText);
        try {
            Optional<List<Board>> result = boardService.searchBoardsByTitle(searchText);
            if(result.isPresent() && !result.get().isEmpty()) {
                return new ResponseEntity<List<Board>>(result.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            }
        } catch(Exception e) {
            return exceptionHandling(e);
        }
    }

    @PostMapping("/{boardId}/post-image")
    public ResponseEntity<?> updatePostImage(@PathVariable Integer boardId,
            @RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = boardService.updatePostImage(boardId, image);
            log.info("imageUrl: {}", imageUrl);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    @GetMapping("/uploads/posts/{filename:.+}")
    public ResponseEntity<Resource> servePostFile(@PathVariable String filename) {
        try {
            File file = new File(uploadPath + "/posts/" + filename);
            Resource resource = new UrlResource(file.toURI());
            if (resource.exists() || resource.isReadable()) {
                // 파일의 MIME 타입 결정
                String contentType = Files.probeContentType(file.toPath());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("파일을 찾을 수 없습니다: {}", filename, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/history")
    public ResponseEntity<?> saveSearchHistory(@RequestBody SearchHistory searchHistory) {
        log.debug("BoardController/saveSearchHistory: searchHistory = {}", searchHistory);
        try {
            Optional<Integer> result = boardService.saveSearchHistory(searchHistory);
            if(result.isPresent() && result.get() > 0) {
                return ResponseEntity.ok(Map.of("message", "검색어 저장 성공"));
            } else {
                return ResponseEntity.ok(Map.of("message", "검색어 저장 실패"));
            }
        } catch (Exception e) {
            log.error("검색어 저장 실패: {}", e);
            return exceptionHandling(e);
        }
    }

    @GetMapping("/history/{userId}/{content}")
    public ResponseEntity<?> getSearchHistory(@PathVariable int userId, @PathVariable String content) {
        log.debug("BoardController/getSearchHistory: userId = {}, content = {}", userId, content);
        try {
            SearchHistory searchHistory = SearchHistory.builder()
                .userId(userId)
                .content(content)
                .build();
            Optional<List<String>> result = boardService.getSearchHistory(searchHistory);
            if(result.isPresent() && !result.get().isEmpty()) {
                return ResponseEntity.ok(result.get());
            } else {
                return ResponseEntity.ok(List.of());
            }
        } catch (Exception e) {
            log.error("연관 검색어 조회 실패: {}", e);
            return exceptionHandling(e);
        }
    }

    private ResponseEntity<String> exceptionHandling(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<String>("Error : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}