package com.web.fitquest.model.board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString 
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor
@RequiredArgsConstructor
public class Board {
    private int id;
    private int userId;      // user 테이블의 id (FK)
    @NonNull private String tag;
    private LocalDateTime date;
    private String writer;   // @NonNull 제거, JOIN으로 가져올 값
    @NonNull private String title;
    @NonNull private String content;
    private int viewCount;
    private int commentCount; // 추가된 필드 - 댓글 수를 저장
    private int hitCount;
}
