package com.example.treino.data.model;

public class Exercise {

    private String uid;
    private String name_exercise;
    private String description_exercise;
    private String image;

    public Exercise() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName_exercise() {
        return name_exercise;
    }

    public void setName_exercise(String name_exercise) {
        this.name_exercise = name_exercise;
    }

    public String getDescription_exercise() {
        return description_exercise;
    }

    public void setDescription_exercise(String description_exercise) {
        this.description_exercise = description_exercise;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
