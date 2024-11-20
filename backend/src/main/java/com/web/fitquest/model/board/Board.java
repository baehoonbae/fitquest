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
    private int userId;
    @NonNull private String tag;
    private LocalDateTime date;
    @NonNull private String writer;
    @NonNull private String title;
    @NonNull private String content;
    private int viewCount;
}
