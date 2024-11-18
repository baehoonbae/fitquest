package com.web.fitquest.model.article;

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
public class Article {
    private int id;
    @NonNull private String title;
    @NonNull private String link;
    @NonNull private String description;
    @NonNull private String date;
}
