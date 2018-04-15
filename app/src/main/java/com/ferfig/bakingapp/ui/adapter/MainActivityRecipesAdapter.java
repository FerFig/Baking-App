package com.ferfig.bakingapp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.Recip;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityRecipesAdapter extends RecyclerView.Adapter<MainActivityRecipesAdapter.RecipsViewHolder>{
    private final Context mContext;

    private final List<Recip> mData;

    public interface OnItemClickListener {
        void onItemClick(Recip recipData);
    }
    private final OnItemClickListener itemClickListener;

    public MainActivityRecipesAdapter(Context mContext, List<Recip> mData, OnItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInftr = LayoutInflater.from(mContext);
        View view = mInftr.inflate(R.layout.recip_card_view, parent, false);
        return new RecipsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipsViewHolder holder, int position) {
        holder.bind(mData.get(position), this.itemClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RecipsViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvRecipName)
        TextView tvRecipName;

        public RecipsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Recip recipData, final OnItemClickListener listener) {
            String recipName = recipData.getName();
            tvRecipName.setText(recipName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener!=null) {
                        listener.onItemClick(recipData);
                    }
                }
            });
        }
    }
}
