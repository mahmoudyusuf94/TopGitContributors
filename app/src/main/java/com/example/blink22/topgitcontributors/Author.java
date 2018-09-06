package com.example.blink22.topgitcontributors;

import com.google.gson.annotations.SerializedName;

public class Author {

    public Author(String login, String avatarUrl){
        this.avatarUrl = avatarUrl;
        this.login = login;
    }

    @SerializedName("login")
    private String login;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("html_url")
    private String profileUrl;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setProfileUrl(String url){
        this.profileUrl = url;
    }

    public String getProfileUrl(){
        return profileUrl;
    }
}