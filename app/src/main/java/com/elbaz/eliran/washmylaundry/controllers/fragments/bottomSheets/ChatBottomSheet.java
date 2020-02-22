package com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.MessageHelper;
import com.elbaz.eliran.washmylaundry.base.BaseBottomSheet;
import com.elbaz.eliran.washmylaundry.models.Message;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.views.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 21-Feb-20.
 */
public class ChatBottomSheet extends BaseBottomSheet implements ChatAdapter.Listener{
    // FOR DESIGN
    @BindView(R.id.chat_layout_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.chat_layout_text_view_recycler_view_empty) TextView textViewRecyclerViewEmpty;
    @BindView(R.id.chat_layout_message_edit_text) TextInputEditText editTextMessage;
    // FOR DATA
    private ChatAdapter mChatAdapter;
    @Nullable
    public static boolean isProvider;


    private Orders mOrders = new Orders();
    private String jsonObject = "{'providerName' : 'userAddress' : 'clientName' : 'reservationDateFormatted' : 'phoneNumber' : 'orderStatus' : 'deliveryPrice' : 'ironingPrice' : 'finalPrice' : 'taxAdded' : 'providerRating' }";

    public static ChatBottomSheet newInstance(String key, String orderJson) {
        Log.d(TAG, "newInstance BottomSheet OrderState: " + key + " " +orderJson);
        ChatBottomSheet chatBottomSheet;
        chatBottomSheet = new ChatBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString(key, orderJson);
        chatBottomSheet.setArguments(bundle);
        return chatBottomSheet ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        // Convert JSON to Order object
        jsonObject = bundle.getString("orderObject");
        mOrders = new Gson().fromJson(jsonObject, Orders.class);

        this.checkCurrentUser();
        this.configureRecyclerView();

        return view;
    }

    private void checkCurrentUser() {
        if(mOrders.getPid().equals(getCurrentUser().getUid())) { isProvider= true; }
        else { isProvider = false; }
    }


    //-------------------
    // CONFIGURATIONS
    //-------------------
    @Override
    protected int getFragmentLayout() {
        return R.layout.bottom_sheet_chat;
    }

    @Override
    protected int setTitle() {
        return 0;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
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
            if(isProvider) {MessageHelper.createMessageForChat(mOrders.getPid(), mOrders.getProviderName(), mOrders.getProviderImageUrl(), editTextMessage.getText().toString(), mOrders.getUniqueOrderId()).addOnFailureListener(this.onFailureListener());}
            else {MessageHelper.createMessageForChat(mOrders.getUid(), mOrders.getClientName(), mOrders.getClientImageUrl(), editTextMessage.getText().toString(), mOrders.getUniqueOrderId()).addOnFailureListener(this.onFailureListener());}

            // Reset text field
            this.editTextMessage.setText("");
        }
    }

    @OnClick(R.id.bottom_sheet_back_button)
    public void onBackButtonClick(){
        dismiss();
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
