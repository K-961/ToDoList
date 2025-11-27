package com.example;

public class TodoItem {
    private String title;
    private String date;
    private boolean done;
    private final long createdTime; // 追加順を保つ

    public TodoItem(String title, String date) {
        this.title = title;
        this.date = date;
        this.done = false;
        this.createdTime = System.currentTimeMillis();
    }

    public String getTitle() { return title; }
    public void setTitle(String s) { title = s; }

    public String getDate() { return date; }
    public void setDate(String d) { date = d; }

    public boolean isDone() { return done; }
    public void setDone(boolean d) { done = d; }

    public long getCreatedTime() { return createdTime; }

    @Override
    public String toString() {
        return title + " (" + date + ")";
    }
}
