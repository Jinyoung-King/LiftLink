package com.jiny.liftlink.auth.data;

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

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
