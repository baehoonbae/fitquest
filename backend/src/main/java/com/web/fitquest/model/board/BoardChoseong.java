package com.web.fitquest.model.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardChoseong {
    private Integer boardId;
    private String titleChoseong;
    private String contentChoseong;
    private String writerChoseong;
}