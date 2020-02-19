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
import com.elbaz.eliran.washmylaundry.models.Provider;

import java.util.List;

/**
 * Created by Eliran Elbaz on 18-Feb-20.
 */
public class ListViewAdapter extends RecyclerView.Adapter<ListViewViewHolder> {
    // DATA
    private List<Provider> mAvailableProvidersList;
    Context mContext;

    public ListViewAdapter(List<Provider> availableProvidersList, Context context) {
        mAvailableProvidersList = availableProvidersList;
        mContext = context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_view_item, parent, false);

        return new ListViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewViewHolder holder, int position) {
        // Get resources to enable text color modification
        Resources resources = holder.itemView.getContext().getResources();
        holder.updateProvidersList(this.mAvailableProvidersList.get(position), Glide.with(mContext), resources);
    }

    @Override
    public int getItemCount() { return this.mAvailableProvidersList.size(); }

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
