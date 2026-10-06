package com.example.focusfuel;
public class Task {
    private String name;
    private String description;
    private String dueDate;
    private String dueTime;
    private boolean isCompleted;

    public Task(String name, String description, String dueDate, String dueTime) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.isCompleted = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        return (isCompleted ? "[✔] " : "[-] ") + name + " - " + dueDate + " " + dueTime;
    }
}

