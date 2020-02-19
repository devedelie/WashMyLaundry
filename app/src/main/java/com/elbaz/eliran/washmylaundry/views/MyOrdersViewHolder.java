package com.elbaz.eliran.washmylaundry.views;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.models.Orders;
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
    @BindView(R.id.my_orders_status_text_container) LinearLayout statusContainer;

    public MyOrdersViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateOrdersList(Orders orders, RequestManager glide, Resources resources){
        try{
          providerName.setText(orders.getProviderName());
          orderDate.setText(String.valueOf(orders.getReservationDateFormatted()));
          orderPrice.setText(String.valueOf(orders.getFinalPrice()));
          orderStatus.setText(Utils.getOrderStatus(orders.getOrderStatus()));
          statusContainer.setBackgroundColor(Utils.getOrderStatusColor(orders.getOrderStatus()));

        }catch (Exception e){
            Log.d(TAG, "updateWorkmatesList: Error " + e);
        }
    }
}
