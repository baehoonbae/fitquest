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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "게시판 API", description = "게시글 CRUD 및 검색 관련 API")
public class BoardController {

    @Value("${upload.path}")
    private String uploadPath;

    private final BoardService boardService;

    @Operation(summary = "전체 게시글 조회", description = "검색 조건에 따른 전체 게시글 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "204", description = "데이터 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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

    @Operation(summary = "특정 게시글 조회", description = "게시글 ID로 특정 게시글을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "204", description = "게시글 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "게시글 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "게시글 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    }) 
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

    @Operation(summary = "게시글 검색", description = "검색 조건에 맞는 게시글을 검색합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "204", description = "검색 결과 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/search")
    public ResponseEntity<?> searchBoards(@RequestBody SearchCondition searchCondition) {
        log.debug("BoardController/searchBoards: searchCondition = {}", searchCondition);
        try {
            Optional<List<Board>> result = boardService.searchBoardsByCondition(searchCondition);
            if (result.isPresent() && !result.get().isEmpty()) {
                return new ResponseEntity<List<Board>>(result.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return exceptionHandling(e);
        }
    }

    @Operation(summary = "게시글 이미지 업로드", description = "게시글에 이미지를 업로드합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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

    @Operation(summary = "게시글 이미지 조회", description = "업로드된 게시글 이미지를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이미지 조회 성공"),
        @ApiResponse(responseCode = "404", description = "이미지 없음")
    })
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

    @Operation(summary = "검색 기록 저장", description = "사용자의 검색 기록을 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "저장 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/history")
    public ResponseEntity<?> saveSearchHistory(@RequestBody SearchHistory searchHistory) {
        log.debug("BoardController/saveSearchHistory: searchHistory = {}", searchHistory);
        try {
            Optional<Integer> result = boardService.saveSearchHistory(searchHistory);
            if (result.isPresent() && result.get() > 0) {
                return ResponseEntity.ok(Map.of("message", "검색어 저장 성공"));
            } else {
                return ResponseEntity.ok(Map.of("message", "검색어 저장 실패"));
            }
        } catch (Exception e) {
            log.error("검색어 저장 실패: {}", e);
            return exceptionHandling(e);
        }
    }

    @Operation(summary = "검색 기록 조회", description = "특정 사용자의 검색 기록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/history/{userId}/{content}")
    public ResponseEntity<?> getSearchHistory(@PathVariable int userId, @PathVariable String content) {
        log.debug("BoardController/getSearchHistory: userId = {}, content = {}", userId, content);
        try {
            SearchHistory searchHistory = SearchHistory.builder()
                    .userId(userId)
                    .content(content)
                    .build();
            Optional<List<String>> result = boardService.getSearchHistory(searchHistory);
            if (result.isPresent() && !result.get().isEmpty()) {
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