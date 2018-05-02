package com.ferfig.bakingapp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailStepAdapter extends RecyclerView.Adapter<RecipeDetailStepAdapter.StepViewHolder>{
    private final Context mContext;

    private final List<Step> mData;

    public interface OnItemClickListener {
        void onItemClick(Step stepData);
    }
    private final OnItemClickListener itemClickListener;

    public RecipeDetailStepAdapter(Context mContext, List<Step> mData, OnItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInftr = LayoutInflater.from(mContext);
        View view = mInftr.inflate(R.layout.steps_card_view, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(mData.get(position), this.itemClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvStepName)
        TextView tvStepName;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Step stepData, final OnItemClickListener listener) {
            String stepName = stepData.getShortDescription();
            tvStepName.setText(stepName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener!=null) {
                        listener.onItemClick(stepData);
                    }
                }
            });
        }
    }
}
