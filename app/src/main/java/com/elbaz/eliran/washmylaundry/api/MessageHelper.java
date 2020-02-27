package com.elbaz.eliran.washmylaundry.api;

import androidx.annotation.Nullable;

import com.elbaz.eliran.washmylaundry.models.Message;
import com.elbaz.eliran.washmylaundry.utils.Utils;
import com.google.android.gms.tasks.Task;
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


    public static Task<Void> createMessageForChat(String id, String name, @Nullable String imageUrl, String textMessage, String chat, boolean isProvider){

        // Create Message unique id with full date
        String messageId = Utils.getDateForOrderId();
        // 1 - Create the Message object
        Message message = new Message(id, name, imageUrl, textMessage, isProvider, messageId);

        // 2 - Store Message to Firestore
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .document(messageId)
                .set(message);
    }

    // --- UPDATE ---

    public static Task<Void> updateMessageReceived(String uniqueOrderId, String dateCreatedId){
        return ChatHelper.getChatCollection().document(uniqueOrderId).collection(COLLECTION_NAME).document(dateCreatedId).update("messageReceived", true);
    }


}
