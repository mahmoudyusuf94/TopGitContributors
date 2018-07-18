package com.example.blink22.topgitcontributors;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Contributor implements Comparable<Contributor> {

    private class Author {

        public Author(String login, String avatarUrl){
            this.avatarUrl = avatarUrl;
            this.login = login;
        }

        @SerializedName("login")
        private String login;

        @SerializedName("avatar_url")
        private String avatarUrl;

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
    }

    @SerializedName("author")
    private Author author;

    @SerializedName("total")
    private Integer count;



    public Contributor(Author author, Integer count){
        this.author = author;
        this.count = count;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getAuthorAvatarUrl(){
        return author.getAvatarUrl();
    }

    public String getAuthorName(){
        return author.getLogin();
    }

    @Override
    public int compareTo(@NonNull Contributor contributor) {
        if(this.count > contributor.getCount()){
            return -1;
        }
        else if( this.count < contributor.getCount()){
            return 1;
        }
        return 0;
    }
}
