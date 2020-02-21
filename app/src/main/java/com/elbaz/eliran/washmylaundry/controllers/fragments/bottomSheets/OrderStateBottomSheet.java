package com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.OrdersHelper;
import com.elbaz.eliran.washmylaundry.base.BaseBottomSheet;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.utils.Utils;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 19-Feb-20.
 */
public class OrderStateBottomSheet extends BaseBottomSheet {
    @BindView(R.id.order_state_provider_name) TextView name;
    @BindView(R.id.order_state_user_address) TextView clientAddress;
    @BindView(R.id.order_state_laundry_bags_amount) TextView laundryBagsAmount;
    @BindView(R.id.order_state_laundry_bags_price) TextView laundryBagsPrice;
    @BindView(R.id.order_state_subtotal_price) TextView subtotalPrice;
    @BindView(R.id.order_state_laundry_fee_price) TextView feePrice;
    @BindView(R.id.order_state_ironing_price) TextView ironingPrice;
    @BindView(R.id.order_state_delivery_price) TextView deliveryPrice;
    @BindView(R.id.order_state_total_price) TextView totalPrice;
    @BindView(R.id.order_state_order_state_text) TextView orderState;
    @BindView(R.id.order_state_ironing_container) LinearLayout ironingContainer;
    @BindView(R.id.order_state_delivery_container) LinearLayout deliveryContainer;
    @BindView(R.id.order_state_waiting_orders_btn) Button statusBtn;


    // FOr Data
    private Orders mOrders = new Orders();
    private String jsonObject = "{'providerName' : 'userAddress' : 'clientName' : 'reservationDateFormatted' : 'phoneNumber' : 'orderStatus' : 'deliveryPrice' : 'ironingPrice' : 'finalPrice' : 'taxAdded' }";

    public static OrderStateBottomSheet newInstance(String key, String orderJson) {
        Log.d(TAG, "newInstance BottomSheet OrderState: " + key + " " +orderJson);
        OrderStateBottomSheet orderStateBottomSheet;
        orderStateBottomSheet = new OrderStateBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString(key, orderJson);
        orderStateBottomSheet.setArguments(bundle);
        return orderStateBottomSheet ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        // Convert JSON to Order object
        jsonObject = bundle.getString("orderObject");
        mOrders = new Gson().fromJson(jsonObject, Orders.class);

        this.setUiElements();

        return view;
    }

    //-------------------
    // CONFIGURATIONS
    //-------------------

    @Override
    protected int getFragmentLayout() {
        return R.layout.bottom_sheet_order_state;
    }

    @Override
    protected int setTitle() {
        return R.string.order_state_title;
    }

    //-------------------
    // ACTIONS
    //-------------------

    @OnClick(R.id.bottom_sheet_back_button)
    public void onBackButtonClick(){
        dismiss();
    }

    @OnClick(R.id.order_state_call_icon)
    public void onCallClick(){
        if(mOrders.getProviderPhone() != 0){
            Intent intent;
            // Check if user or provider
            if(mOrders.getPid().equals(getCurrentUser().getUid())){ intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mOrders.getProviderPhone()));}
            else{ intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mOrders.getUserPhone()));}
            // Open Dialer
            startActivity(intent);
        }else{
            // No phone
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.no_available_phone_number),Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.order_state_chat_icon)
    public void onChatClick(){
        // Start Chat
    }

    @OnClick (R.id.order_state_waiting_orders_btn)
    public void onStatusBtnClick(){
        int i = mOrders.getOrderStatus();
        if(i<4) {
            i++;
            OrdersHelper.updateOrderState(mOrders.getUniqueOrderId(), i);
            dismiss();
        }
    }

    //-------------------
    // UI
    //-------------------

    private void setUiElements() {
        try {
            // Set USER/PROVIDER name
            if(mOrders.getPid().equals(getCurrentUser().getUid())){ name.setText(mOrders.getClientName());}
            else{ name.setText(mOrders.getProviderName());}
            clientAddress.setText(mOrders.getClientAddress());
            laundryBagsAmount.setText(getString(R.string.reservation_laundry_bags_amount,String.valueOf(mOrders.getOrderBagsAmount())));
            laundryBagsPrice.setText(getString(R.string.laundry_bags_price, String.valueOf(mOrders.getSubtotalPrice())));
            subtotalPrice.setText(getString(R.string.laundry_subtotal_price, String.valueOf(mOrders.getSubtotalPrice())));
            feePrice.setText(getString(R.string.laundry_fees_price, String.valueOf(mOrders.getTaxAdded())));
            if(mOrders.getIroningPrice() != 0){
                ironingContainer.setVisibility(View.VISIBLE);
                ironingPrice.setText(getString(R.string.laundry_ironing_price, String.valueOf(mOrders.getIroningPrice())));
            }
            if(mOrders.getDeliveryPrice() != 0){
                ironingContainer.setVisibility(View.VISIBLE);
                deliveryPrice.setText(getString(R.string.laundry_delivery_price, String.valueOf(mOrders.getDeliveryPrice())));
            }
            totalPrice.setText(getString(R.string.laundry_total_price, String.valueOf(mOrders.getFinalPrice())));
            orderState.setText(Utils.getOrderStatus(mOrders.getOrderStatus()));

            // Show button to provider only
            if(mOrders.getPid().equals(getCurrentUser().getUid())){
                statusBtn.setVisibility(View.VISIBLE) ;
                statusBtn.setText(getStateString(mOrders.getOrderStatus()));
                // If order was delivered, set a green and un-clickable button
                if(mOrders.getOrderStatus() == 4){
                    statusBtn.setClickable(false);
                    statusBtn.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.order_delivered));
                }
            }

        }catch (Exception e){
            Log.d(TAG, "setUiElements: ERROR" + e);
        }
    }

    private String getStateString(int i){
        String status="";
        switch (i){
            case 1:
                return getString(R.string.order_state_provider_accept_btn);
            case 2:
                return getString(R.string.order_state_provider_mark_as_ready);
            case 3:
                return getString(R.string.order_state_provider_mark_as_delivered);
            case 4:
                return getString(R.string.order_state_provider_delivered);
        }

        return status;
    }
}
