package com.elbaz.eliran.washmylaundry.views;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.models.Orders;

import java.util.List;

/**
 * Created by Eliran Elbaz on 18-Feb-20.
 */
public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersViewHolder> {
    // DATA
    private List<Orders> mOrdersList;
    Context mContext;

    public MyOrdersAdapter(List<Orders> ordersList, Context context) {
        mOrdersList = ordersList;
        mContext = context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public MyOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.my_orders_item, parent, false);

        return new MyOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrdersViewHolder holder, int position) {
// Get resources to enable text color modification
        Resources resources = holder.itemView.getContext().getResources();
        holder.updateOrdersList(this.mOrdersList.get(position), Glide.with(mContext), resources);
    }

    @Override
    public int getItemCount() {
        return mOrdersList.size();
    }

    //setHasStableIds(true) & the methods below are making an optimization while providing data to ViewHolder, to keep id as unique and unchangeable.
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
