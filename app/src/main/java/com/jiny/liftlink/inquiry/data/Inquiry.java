package com.jiny.liftlink.inquiry.data;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class Inquiry {
    @DocumentId
    private String id;
    private String vehicle;
    private String height;
    /// 작업 내용
    private String work;
    /// 지역
    private String region;



    /// 작업 날짜
    private String date;
    /// 소요시간
    private String duration;
    private Date timestamp;
    private String userId;

    // 기본 생성자
    public Inquiry() {
    }

    // 생성자
    public Inquiry(String id, String duration, String height, String region, Date timestamp, String userId, String vehicle, String work) {
        this.id = id;
        this.duration = duration;
        this.height = height;
        this.region = region;
        this.timestamp = timestamp;
        this.userId = userId;
        this.vehicle = vehicle;
        this.work = work;
    }

    public Inquiry(String date, String id, String vehicle, String height, String work, String region, String duration, Date timestamp, String userId) {
        this.date = date;
        this.id = id;
        this.vehicle = vehicle;
        this.height = height;
        this.work = work;
        this.region = region;
        this.duration = duration;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Getter 및 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getDate() {
        return date;
    }
}

