package com.web.fitquest.model.user;

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
public class User {
    private int id;
    @NonNull private String email;
    @NonNull private String password;
    @NonNull private String name;
    private int isAdmin;
    private String description;
}
