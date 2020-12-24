package com.example.blogs;

public class Blog {
    //set variable
    private String image, title, id, description, date, penulis;
    private long waktu;

    //empty constructor
    public Blog() {
    }

    public Blog(String image, String title, String id, String description, String date, String penulis, long waktu) {
        this.image = image;
        this.title = title;
        this.id = id;
        this.description = description;
        this.date = date;
        this.penulis = penulis;
        this.waktu = waktu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPenulis() {
        return penulis;
    }

    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    public long getWaktu() {
        return waktu;
    }

    public void setWaktu(long waktu) {
        this.waktu = waktu;
    }
}