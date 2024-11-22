package com.web.fitquest.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class SearchHistory {
    private int id;
    private int userId;
    private String content;
    private LocalDateTime createdAt;
}