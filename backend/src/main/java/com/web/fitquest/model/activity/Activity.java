package com.web.fitquest.model.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString 
@Getter 
@Setter 
@AllArgsConstructor
public class Activity {
    private int id;
    private int userId;
    private String date;
    private double ratio;

}
