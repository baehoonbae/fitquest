package com.web.fitquest.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class TodoRequest {
    private int userId;
    private int categoryId;
    private String year;
    private String month;
}
