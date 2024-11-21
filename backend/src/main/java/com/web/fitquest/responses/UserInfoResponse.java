package com.web.fitquest.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
    private int id;
    private String email;
    private String name;
    private String profileImage;
    private int isAdmin;
    private String description;
}
