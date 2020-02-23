package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.MessageHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.models.Message;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.views.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

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
    @Nullable
    public static boolean isProvider;

    private Orders mOrders = new Orders();
    private String jsonObject = "{'providerName' : 'userAddress' : 'clientName' : 'reservationDateFormatted' : 'phoneNumber' : 'orderStatus' : 'deliveryPrice' : 'ironingPrice' : 'finalPrice' : 'taxAdded' : 'providerRating' }";

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

        this.checkCurrentUser();
        this.configureRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle();
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


    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.chat_layout_send_button)
    public void onClickSendMessage() {
        //  Check if text field is not empty and current user properly downloaded from Firestore
        if (!TextUtils.isEmpty(editTextMessage.getText())){
            //  Create a new Message to Firestore (USER/PROVIDER)
            if(isProvider) {
                MessageHelper.createMessageForChat(mOrders.getPid(), mOrders.getProviderName(), mOrders.getProviderImageUrl(), editTextMessage.getText().toString(), mOrders.getUniqueOrderId(), true).addOnFailureListener(this.onFailureListener());}
            else {MessageHelper.createMessageForChat(mOrders.getUid(), mOrders.getClientName(), mOrders.getClientImageUrl(), editTextMessage.getText().toString(), mOrders.getUniqueOrderId(), false).addOnFailureListener(this.onFailureListener());}

            // Reset text field
            this.editTextMessage.setText("");
        }
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
