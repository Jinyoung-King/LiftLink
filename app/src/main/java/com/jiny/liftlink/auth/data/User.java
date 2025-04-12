package com.jiny.liftlink.auth.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private String name;
    private String email;
    private String phone;
    private String address;
    private String profileImageUrl;  // 프로필 이미지 URL 추가

    // 기본 생성자 필요
    public User() {
    }

    public User(String name, String email, String phone, String address, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
    }

}
