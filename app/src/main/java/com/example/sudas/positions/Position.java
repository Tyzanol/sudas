package com.example.sudas.positions;

public class Position {
    private String title;
    private String location;
    private String recruiterId;
    private String positionId;

    public Position (String title, String location, String recruiterId, String positionId) {
        this.recruiterId = recruiterId;
        this.title = title;
        this.location = location;
        this.positionId = positionId;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getRecruiterId() {
        return recruiterId;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRecruiterId(String recruiterId) {
        this.recruiterId = recruiterId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }
}
