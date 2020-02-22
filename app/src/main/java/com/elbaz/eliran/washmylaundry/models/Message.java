package com.elbaz.eliran.washmylaundry.models;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Eliran Elbaz on 21-Feb-20.
 */
public class Message {
    private String id, name, message;
    private Date dateCreated;
    @Nullable
    private String urlImage;

    public Message() { }

    public Message(String id, String name, String message) {
        this.id = id;
        this.name = name;
        this.message = message;
    }

    public Message(String id, String name, @Nullable String urlImage , String message) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.urlImage = urlImage;
    }


    // --- GETTERS ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    @Nullable
    public String getUrlImage() { return urlImage; }


    // --- SETTERS ---
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUrlImage(@Nullable String urlImage) { this.urlImage = urlImage; }




}