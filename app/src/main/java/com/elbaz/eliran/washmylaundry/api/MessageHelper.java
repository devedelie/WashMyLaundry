package com.elbaz.eliran.washmylaundry.api;

import androidx.annotation.Nullable;

import com.elbaz.eliran.washmylaundry.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

/**
 * Created by Eliran Elbaz on 21-Feb-20.
 */
public class MessageHelper {
    private static final String COLLECTION_NAME = "messages";

    // --- GET ---

    public static Query getAllMessageForChat(String chat){
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    // --- UPDATE ---


    public static Task<DocumentReference> createMessageForChat(String id, String name, @Nullable String imageUrl, String textMessage, String chat, boolean isProvider){

        // 1 - Create the Message object
        Message message = new Message(id, name, imageUrl, textMessage, isProvider);

        // 2 - Store Message to Firestore
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
    }

}
