package com.example.sudas.matches;

public class Matches {
    private String userId;
    private String positionId;
    private String name;
    private String profileImageUrl;
    public Matches (String userId, String name, String profileImageUrl){
        this.userId = userId;
//        this.positionId = positionId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getUserId(){
        return userId;
    }
    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}