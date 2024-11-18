package com.web.fitquest.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TodoRequest {
    private int userId;
    private int categoryId;
}
