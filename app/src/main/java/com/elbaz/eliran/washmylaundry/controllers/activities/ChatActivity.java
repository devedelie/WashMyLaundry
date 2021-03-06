package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.ChatHelper;
import com.elbaz.eliran.washmylaundry.api.MessageHelper;
import com.elbaz.eliran.washmylaundry.api.OrdersHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.models.Message;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.repositories.CurrentUserDataRepository;
import com.elbaz.eliran.washmylaundry.utils.Utils;
import com.elbaz.eliran.washmylaundry.viewmodel.CurrentUserSharedViewModel;
import com.elbaz.eliran.washmylaundry.views.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 23-Feb-20.
 */
public class ChatActivity extends BaseActivity implements ChatAdapter.Listener{
    // FOR DESIGN
    @BindView(R.id.chat_layout_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.chat_layout_text_view_recycler_view_empty) TextView textViewRecyclerViewEmpty;
    @BindView(R.id.chat_top_bar_title) TextView topTitle;
    @BindView(R.id.chat_layout_message_edit_text) TextInputEditText editTextMessage;
    // FOR DATA
    private ChatAdapter mChatAdapter;
    private CurrentUserSharedViewModel mUserSharedViewModel;
    @Nullable
    public static boolean isProvider;

    private Orders mOrders = new Orders();
    private String jsonObject = "{'providerName' : 'userAddress' : 'clientName' : 'reservationDateFormatted' : 'phoneNumber' : 'orderStatus' : 'deliveryPrice' : 'ironingPrice' : 'finalPrice' : 'taxAdded' : 'providerRating' : 'clientEmail' : 'providerEmail' }";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) { //Null Checking
            // Convert JSON to Order object
            jsonObject = intent.getStringExtra("orderObject");
            mOrders = new Gson().fromJson(jsonObject, Orders.class);
            Log.d(TAG, "onCreate Chat: " + mOrders.getProviderName());
        }

        this.configureViewModel();
        // Activate listeners for current chat channel (to indicate on firestore that the message was seen)
        this.activateChatListeners(mOrders.getUniqueOrderId());
        this.checkCurrentUser();
        this.configureRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detach the listener, so the event callbacks stop getting called.
        CurrentUserDataRepository.getInstance().stopListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public int getActivityLayout() {
        return R.layout.activity_chat;
    }

    //-------------------
    // CONFIGURATIONS
    //-------------------

    private void configureViewModel() {
        mUserSharedViewModel = new ViewModelProvider(this).get(CurrentUserSharedViewModel.class);
        mUserSharedViewModel.init();
    }

    private void checkCurrentUser() {
        if(mOrders.getPid().equals(getCurrentUser().getUid())) { isProvider= true; }
        else { isProvider = false; }
    }

    // Configure RecyclerView with a Query
    private void configureRecyclerView(){
        //Configure Adapter & RecyclerView
        this.mChatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(mOrders.getUniqueOrderId())), Glide.with(this), this.getCurrentUser().getUid(), this);
        mChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.mChatAdapter);
    }

    //  Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // Activate chat listeners by chat channel
    private void activateChatListeners(String chatChannel){
        Log.d(TAG, "activateChatListeners: " + chatChannel);
        mUserSharedViewModel.setMessagesList(chatChannel);
        getMessagesFromListener(chatChannel);
    }

    private void getMessagesFromListener(String uniqueOrderId){
        mUserSharedViewModel.getMessagesList().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                // Receive message list and filter isReceived / isSeen
                for(int i = 0 ; i < messages.size(); i++){
                    // Apply the action only if there are unreceived messages in the list, and only if the message userId is different than the current user
                    if(!messages.get(i).isMessageReceived()&& !messages.get(i).getId().equals(getCurrentUser().getUid())){
                        // Mark the chat document with value 'messageReceived' as true
                        MessageHelper.updateMessageReceived(uniqueOrderId, messages.get(i).getMessageDateId());
                    }
                }
            }
        });
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.chat_layout_send_button)
    public void onClickSendMessage() {
        //  Check if text field is not empty
        if (!TextUtils.isEmpty(editTextMessage.getText())){
            //  Create a new Message to Firestore (USER/PROVIDER)
            if(isProvider) {
                MessageHelper.createMessageForChat(mOrders.getPid(), mOrders.getProviderName(), mOrders.getProviderImageUrl(), editTextMessage.getText().toString(), mOrders.getUniqueOrderId(), true).addOnFailureListener(this.onFailureListener());}
            else {MessageHelper.createMessageForChat(mOrders.getUid(), mOrders.getClientName(), mOrders.getClientImageUrl(), editTextMessage.getText().toString(), mOrders.getUniqueOrderId(), false).addOnFailureListener(this.onFailureListener());}

            // Mark chat as activated in Order document, for channel listener
            OrdersHelper.getOrdersCollection().document(mOrders.getUniqueOrderId()).update("chatActivated", true);

            // Start a Task to send an email, if the other side did not receive the message in chat (probably offline)
            sendEmailNotification(editTextMessage.getText().toString());

            // Reset text field
            this.editTextMessage.setText("");
        }
    }

    private void sendEmailNotification(String message) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if there are unseen chat messages (which the receiver-side did not receive)
                ChatHelper.getChatCollection().document(mOrders.getUniqueOrderId()).collection("messages").whereEqualTo("messageReceived", false).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() >0){
                            Log.d(TAG, "onSuccess: sendEMailNotification" + mOrders.getProviderEmail() + "  " + mOrders.getClientEmail());
                            // There are unseen messages and we need to send email notification
                            if(isProvider) {Utils.sendEmailWithRetrofit(getString(R.string.from_chat), mOrders.getClientEmail(), getString(R.string.you_have_a_new_message_from_x, mOrders.getProviderName()), getString(R.string.x_message, message));}
                            else {Utils.sendEmailWithRetrofit(getString(R.string.from_chat), mOrders.getProviderEmail(), getString(R.string.you_have_a_new_message_from_x, mOrders.getClientName()), getString(R.string.x_message, message));}
                        }
                    }
                });
            }
        }, 2000); // Wait 2 seconds before executing the function
    }

    @OnClick(R.id.chat_back_button)
    public void onBackButtonClick(){
        finish();
    }

    // --------------------
    // UI
    // --------------------

    private void setTitle(){
        if(isProvider){ topTitle.setText(mOrders.getClientName());}
        else {topTitle.setText(mOrders.getProviderName());}
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        // Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(mChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
