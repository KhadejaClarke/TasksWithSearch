package com.khadejaclarke.taskswithsearch;

public class Task {
    private int id;
    private String title;
    private String description;
    private Boolean done;

    // Default Constructor
    public Task() {}

    // Parameterised Constructors
    public Task(String title, String description, Boolean done) {
        this.title = title;
        this.description = description;
        this.done = done;
    }

    public Task(int id, String title, String description, Boolean done) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", done=" + done +
                '}';
    }
}
