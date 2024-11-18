package com.web.fitquest.model.todo;

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
public class Todo {
    private int id;
    private int userId;
    @NonNull private Integer categoryId;
    @NonNull private Integer isDone;
    @NonNull private String content;
    @NonNull private String date;
}