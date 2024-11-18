package com.web.fitquest.model.comment;

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
public class Comment {
    private int id;
    private int boardId;
    private int userId;
    @NonNull private String writer;
    @NonNull private String content; 
    @NonNull private String date;
    private int parentId;
    private int isDelete;
}
