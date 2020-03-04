package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.MessageHelper;
import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets.EditProviderBottomSheet;
import com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets.OrderStateBottomSheet;
import com.elbaz.eliran.washmylaundry.models.Message;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.repositories.CurrentUserDataRepository;
import com.elbaz.eliran.washmylaundry.utils.ItemClickSupport;
import com.elbaz.eliran.washmylaundry.utils.Utils;
import com.elbaz.eliran.washmylaundry.viewmodel.CurrentUserSharedViewModel;
import com.elbaz.eliran.washmylaundry.viewmodel.ProviderViewModel;
import com.elbaz.eliran.washmylaundry.views.MyOrdersAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.elbaz.eliran.washmylaundry.models.Constants.CHANNEL_ID;

public class MainProviderActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.main_provider_activity_drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.drawer_main_provider_activity) NavigationView navigationView;
    @BindView(R.id.id_details_profile_image) ImageView providerImage;
    @BindView(R.id.id_details_username_text) TextView usernameText;
    @BindView(R.id.id_details_email_text) TextView emailText;
    @BindView(R.id.id_details_phone_text) TextView phoneText;
    @BindView(R.id.id_details_address_text) TextView addressText;
    @BindView(R.id.id_details_status_text) TextView statusText;
    @BindView(R.id.provider_availability_on_off_text) TextView availabilityOnOffText;
    @BindView(R.id.provider_delivery_on_off_text) TextView deliveryOnOffText;
    @BindView(R.id.provider_ironing_on_off_text) TextView ironingOnOffText;
    @BindView(R.id.provider_availability_img) ImageView statusIndicator;
    @BindView(R.id.provider_status_switch) Switch statusSwitch;
    @BindView(R.id.provider_delivery_switch) Switch deliverySwitch;
    @BindView(R.id.provider_ironing_switch) Switch ironingSwitch;
    @BindView(R.id.provider_max_bags_picker_text) EditText maxBagsEditText;
    @BindView(R.id.provider_price_picker_text) EditText priceEditText;
    @BindView(R.id.providers_scheduled_orders_recycler_view) RecyclerView ordersRecyclerView;
    // Data
    private ProviderViewModel mProviderViewModel;
    private CurrentUserSharedViewModel mUserSharedViewModel;
    private Provider mProvider;
    private List<Orders> allProviderOrders;
    private List<Orders> mRecentOrdersList;
    private MyOrdersAdapter mMyOrdersAdapter;
    private SharedPreferences mSharedPreferences;
    private int lastProviderOrdersCount , currentProviderOrdersCount;
    static boolean active = false;
    private List<Orders> mOrdersList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        configureViewModel();
        configureRecyclerView();
        configureOnClickRecyclerView();
        configureDrawerLayoutAndNavigationView();
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataObserver();
    }


    private void setDataObserver() {
        mProviderViewModel.getCurrentProviderData().observe(this, new Observer<Provider>() {
            @Override
            public void onChanged(Provider provider) {
                mProvider = new Provider();
                mProvider = provider;
                updateUiWithData(mProvider);
                configureSwitches();
            }
        });
        mProviderViewModel.getOrdersList().observe(this, new Observer<List<Orders>>() {
            @Override
            public void onChanged(List<Orders> orders) {
                mOrdersList.clear();
                mOrdersList.addAll(orders);
                manageNotificationForNewOrders(orders);
                updateUI(orders);
                // Activate listeners for all existing chat channels (with UniqueOrderId)
                for (int i = 0 ; i < orders.size() ; i++){
                    if(mOrdersList.get(i).isChatActivated())
                        activateChatListeners(orders.get(i).getUniqueOrderId());
                }
            }
        });
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
                    // Apply the action only if there are unreceived messages in the list, and only if the message userId is different than the current provider (to avoid self notification)
                    if(!messages.get(i).isMessageReceived() && !messages.get(i).getId().equals(getCurrentUser().getUid())){
                        Log.d(TAG, "onChanged: Message RECEIVED");
                        Utils.createNotification(MainProviderActivity.this, ProviderOrdersActivity.class, CHANNEL_ID, getString(R.string.title_new_message, messages.get(i).getName()), getString(R.string.content_message_from, messages.get(i).getMessage()), messages.get(i).getMessage());
                        // Mark the message as received in Firestore
                        MessageHelper.updateMessageReceived(uniqueOrderId, messages.get(i).getMessageDateId());

                    }
                    if(!messages.get(i).isMessageSeen()){

                        Log.d(TAG, "onChanged: Message SEEN");
                    }
                }
            }
        });
    }

    private void manageNotificationForNewOrders(List<Orders> orders) {
        mSharedPreferences = getSharedPreferences("lastProviderOrdersCount", MODE_PRIVATE);
        lastProviderOrdersCount= mSharedPreferences.getInt("lastProviderOrdersCount", 0);
        Log.d(TAG, "manageNotificationForNewOrders:  last=" + lastProviderOrdersCount + " size:" +orders.size());
        if(lastProviderOrdersCount < orders.size()){
            Utils.createNotification(this, ProviderOrdersActivity.class, CHANNEL_ID, getString(R.string.new_order_received_title), getString(R.string.new_order_received_content) , "subject" );
            SharedPreferences.Editor editor = getSharedPreferences("lastProviderOrdersCount", MODE_PRIVATE).edit();
            editor.putInt("lastProviderOrdersCount", orders.size());
            editor.apply();
        }

    }


    @Override
    public int getActivityLayout() {
        return R.layout.activity_main_provider;
    }

    //-------------------
    // CONFIGURATIONS
    //-------------------

    private void configureViewModel() {
        mProviderViewModel = new ViewModelProvider(this).get(ProviderViewModel.class);
        mUserSharedViewModel = new ViewModelProvider(this).get(CurrentUserSharedViewModel.class);
        mUserSharedViewModel.init();
        mProviderViewModel.initProvider(); // To retrieve the data from the repository
        mProviderViewModel.setCurrentProviderData(); // Trigger the Document listener
        mProviderViewModel.setOrderList(CurrentUserDataRepository.currentUserID); // Trigger orders listener
    }

    private void configureRecyclerView() {
        //Configure Adapter & RecyclerView
        ordersRecyclerView.setHasFixedSize(true);
        mRecentOrdersList = new ArrayList<>();
        mMyOrdersAdapter = new MyOrdersAdapter(this.mRecentOrdersList, this);
        ordersRecyclerView.setAdapter(this.mMyOrdersAdapter);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //  Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(ordersRecyclerView, R.layout.fragment_orders)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // RecyclerView onClick action
                        OrderStateBottomSheet.newInstance("orderObject", new Gson().toJson(mRecentOrdersList.get(position))).show(getSupportFragmentManager(), "OrderInvoice");
                    }
                });
    }

    // Navigation drawer config
    protected void configureDrawerLayoutAndNavigationView(){
        // Configure drawer layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configure NavigationView & set item selection listener
        View headerView = this.navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.navigation_header_name);
        TextView userEmail = headerView.findViewById(R.id.navigation_header_email);
        ImageView userImage = headerView.findViewById(R.id.navigation_header_image);
        // set user name and email
        userName.setText(this.getCurrentUser().getDisplayName());
        userEmail.setText(this.getCurrentUser().getEmail());
        // Set Image
        if (this.getCurrentUser() != null) {
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
            }
        }
        // Set listener
        navigationView.setNavigationItemSelectedListener(this);
    }

        private void configureSwitches() {
            // ----- Availability Switch -------//
            if(mProvider.getProviderAddress() == null || mProvider.getProviderAddress().isEmpty()){
                statusSwitch.setClickable(false);
                alertDialogInformation(getString(R.string.provider_missing_address_title), getString(R.string.provider_missing_address_content));
            }else {
                statusSwitch.setClickable(true);
                statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){ availabilityOnOffText.setText(getString(R.string.provider_availability_on));}
                    else{ availabilityOnOffText.setText(getString(R.string.provider_availability_off)); }
                    ProviderHelper.updateProviderAvailabilityStatus(CurrentUserDataRepository.currentUserID, isChecked);
                }
                });
            }
            // ----- Delivery Switch -------//
            deliverySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(deliverySwitch.isChecked()){ deliveryOnOffText.setText(getString(R.string.provider_delivery_on));}
                    else{ deliveryOnOffText.setText(getString(R.string.provider_delivery_off)); }
                    ProviderHelper.updateProviderDeliveryStatus(CurrentUserDataRepository.currentUserID, isChecked);
                }
            });
            // ----- Ironing Switch -------//
            ironingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(ironingSwitch.isChecked()){ ironingOnOffText.setText(getString(R.string.provider_ironing_on));}
                    else{ ironingOnOffText.setText(getString(R.string.provider_ironing_off)); }
                    ProviderHelper.updateProviderIroningStatus(CurrentUserDataRepository.currentUserID, isChecked);
                }
            });
    }


    //-------------------
    // ACTIONS
    //-------------------

    @OnClick(R.id.provider_max_bags_picker_text)
    public void onWeightTextClick(){
        pricePickerDialog(getString(R.string.provider_max_weight_dialog), Integer.parseInt(maxBagsEditText.getText().toString()), 1, 5);
    }

    @OnClick(R.id.provider_price_picker_text)
    public void onPriceTextClick(){
        pricePickerDialog(getString(R.string.provider_price_rate_dialog), Integer.parseInt(priceEditText.getText().toString().replaceAll("[€]", "")), 3, 15);
    }

    // Drawer item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int order = menuItem.getOrder();
        Log.d(TAG, "Test onNavigationItemSelected: "+ order);
        switch (order){
            case 0:
                 // Your orders
                Intent intent = new Intent(this, ProviderOrdersActivity.class);
                startActivity(intent);
                break;
            case 1:
                 // Profile action
                onEditClick();
                break;
            case 2:
                signOutUserFromFirebase(); // logout action
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.edit_provider_icon)
    public void onEditClick(){
        EditProviderBottomSheet.newInstance("providerObject", new Gson().toJson(mProvider)).show(getSupportFragmentManager(), "editProvider");
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void updateProviderMaxWeight(int maxWeight){
        ProviderHelper.updateProviderWeightPerService(CurrentUserDataRepository.currentUserID, maxWeight).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Max Weight Saved");
            }
        });
    }

    private void updateProviderPrice(int pricePerKg){
        ProviderHelper.updateProviderPricePerKg(CurrentUserDataRepository.currentUserID, pricePerKg).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Price Saved");
            }
        });
    }

    //-------------------
    // UI
    //-------------------

    private void updateUiWithData(Provider provider) {
        Log.d(TAG, "updateUiWithData: ");
        // Set provider Image //
        if (this.getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(this.getCurrentUser().getPhotoUrl())
                    .apply(RequestOptions.centerCropTransform())
                    .into(providerImage);
        }
        usernameText.setText(provider.getProviderName());
        emailText.setText(getCurrentUser().getEmail());
        // Phone //
        if(provider.getPhoneNumber() == 0){ phoneText.setText(R.string.provider_phone_not_available);}
        else{phoneText.setText(String.valueOf(provider.getPhoneNumber()));}
        // Address //
        if(provider.getProviderAddress() == null){addressText.setText(R.string.provider_address_not_available);}
        else{addressText.setText(provider.getProviderAddress());}
        // Status //
        if(provider.getIsAvailable()) {statusText.setText(R.string.provider_status_available); statusIndicator.setImageResource(R.drawable.ic_button_icon_green); statusSwitch.setChecked(true); availabilityOnOffText.setText(getString(R.string.provider_availability_on));}
        else{statusText.setText(R.string.provider_status_not_available); statusIndicator.setImageResource(R.drawable.ic_button_icon_red); statusSwitch.setChecked(false);}
        // Delivery //
        if(provider.getIsDelivering()) {deliveryOnOffText.setText(R.string.provider_delivery_on);  deliverySwitch.setChecked(true);}
        else{deliveryOnOffText.setText(R.string.provider_delivery_off); deliverySwitch.setChecked(false);}
        // Ironing //
        if(provider.getIsIroning()) {ironingOnOffText.setText(R.string.provider_ironing_on);  ironingSwitch.setChecked(true);}
        else{ironingOnOffText.setText(R.string.provider_ironing_off); ironingSwitch.setChecked(false);}
        // Max Bags //
        maxBagsEditText.setText(String.valueOf(provider.getMaxBags()));
        // Price //
        priceEditText.setText(getString(R.string.euro_value, String.valueOf(provider.getPricePerKg())));

    }

    // Update recyclerView with orders list
    private void updateUI(List<Orders> orders) {
        allProviderOrders = new ArrayList<>();
        allProviderOrders.clear();
        allProviderOrders.addAll(orders);
        // Add only unfinished orders
        mRecentOrdersList.clear();
        for(int i = 0 ; i < allProviderOrders.size() ; i++){
            if(allProviderOrders.get(i).getOrderStatus() < 4){
                mRecentOrdersList.add(allProviderOrders.get(i));
            }
        }
        // Sort orders (newest first)
        if(mRecentOrdersList != null && mRecentOrdersList.size() > 0){
            Collections.sort(mRecentOrdersList, new Comparator<Orders>() {
                @Override
                public int compare(Orders orders, Orders t1) {
                    int sort;
                    sort = orders.getReservationDate().compareTo(t1.getReservationDate());
                    return sort;
                }
            });
        }
        // Notify changes
        mMyOrdersAdapter.notifyDataSetChanged();
    }

    public void pricePickerDialog(String title, int displayCurrentValue, int minValue, int maxValue) {
        final Dialog dialog = new Dialog(MainProviderActivity.this);
        dialog.setContentView(R.layout.number_picker_dialog);
        Button saveBtn = (Button) dialog.findViewById(R.id.number_picker_save);
        TextView dialogTitle = (TextView) dialog.findViewById(R.id.picker_dialog_title);
        dialogTitle.setText(title);
        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);
        np.setMaxValue(maxValue); // max value depends on the calling method
        np.setMinValue(minValue);   // min value depends on the calling method
        np.setValue(displayCurrentValue);
        np.setWrapSelectorWheel(false);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Save value in Firestore
                if(title.equals(getString(R.string.provider_price_rate_dialog))) updateProviderPrice(np.getValue());
                if(title.equals(getString(R.string.provider_max_weight_dialog))) updateProviderMaxWeight(np.getValue());
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

}


