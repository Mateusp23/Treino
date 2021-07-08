package com.example.treino.data.model;

public class Workout {

    private String uid;
    private String name;
    private String description;

    public Workout() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "\n" + name +" \n\n" + description+"\n";
    }
}
