package com.elbaz.eliran.washmylaundry.models;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Eliran Elbaz on 21-Feb-20.
 */
public class Message {
    private String id, name, message, messageDateId;
    private Date dateCreated;
    @Nullable
    private String urlImage;
    boolean isProvider, isMessageReceived, isMessageSeen;

    public Message() { }

    public Message(String id, String name, @Nullable String urlImage , String message, boolean isProvider, String messageDateId) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.urlImage = urlImage;
        this.isProvider = isProvider;
        this.messageDateId = messageDateId;
    }


    // --- GETTERS ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    @Nullable
    public String getUrlImage() { return urlImage; }
    public boolean isProvider() { return isProvider; }

    // --- SETTERS ---
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUrlImage(@Nullable String urlImage) { this.urlImage = urlImage; }
    public void setProvider(boolean provider) { isProvider = provider; }

    public boolean isMessageReceived() {
        return isMessageReceived;
    }

    public void setMessageReceived(boolean messageReceived) {
        isMessageReceived = messageReceived;
    }

    public boolean isMessageSeen() {
        return isMessageSeen;
    }

    public void setMessageSeen(boolean messageSeen) {
        isMessageSeen = messageSeen;
    }

    public String getMessageDateId() {
        return messageDateId;
    }

    public void setMessageDateId(String messageDateId) {
        this.messageDateId = messageDateId;
    }
}