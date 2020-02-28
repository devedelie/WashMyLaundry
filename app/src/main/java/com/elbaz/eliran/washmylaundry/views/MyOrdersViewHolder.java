package com.elbaz.eliran.washmylaundry.views;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.repositories.CurrentUserDataRepository;
import com.elbaz.eliran.washmylaundry.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 18-Feb-20.
 */
public class MyOrdersViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.my_orders_recyclerView_provider_name) TextView providerName;
    @BindView(R.id.my_orders_recyclerView_date) TextView orderDate;
    @BindView(R.id.my_orders_recyclerView_price) TextView orderPrice;
    @BindView(R.id.my_orders_recyclerView_status) TextView orderStatus;
    @BindView(R.id.my_orders_status_text_container) CardView statusContainer;
    @BindView(R.id.unseen_message_chat_icon) LinearLayout chatIcon;

    public MyOrdersViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateOrdersList(Orders orders, RequestManager glide, Resources resources){
        try{
            if(orders.getPid().equals(CurrentUserDataRepository.currentUserID)) {providerName.setText(resources.getString(R.string.my_orders_recyclerView_client_name, orders.getClientName()));} // If current user is a Provider
            else {providerName.setText(resources.getString(R.string.my_orders_recyclerView_provider_name, orders.getProviderName()));} // // If current user is a Client
            orderDate.setText(resources.getString(R.string.my_orders_recyclerView_date, String.valueOf(orders.getReservationDateFormatted())));
            orderPrice.setText(resources.getString(R.string.my_order_recyclerView_price, String.valueOf(orders.getFinalPrice())));
            orderStatus.setText(Utils.getOrderStatus(orders.getOrderStatus()));
            statusContainer.setCardBackgroundColor(resources.getColor(Utils.getOrderStatusColor(orders.getOrderStatus())));
            // Display chat icon when there are unseen messages
            if(orders.isContainUnseenMessages()){
                chatIcon.setVisibility(View.VISIBLE);
            }else {
                chatIcon.setVisibility(View.INVISIBLE);
            }

        }catch (Exception e){
            Log.d(TAG, "updateWorkmatesList: Error " + e);
        }
    }
}
