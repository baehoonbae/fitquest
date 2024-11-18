package com.web.fitquest.model.hit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString 
@Getter 
@Setter 
@AllArgsConstructor
public class Hit {
    private int id;
    private int boardId;
    private int userId;
}
