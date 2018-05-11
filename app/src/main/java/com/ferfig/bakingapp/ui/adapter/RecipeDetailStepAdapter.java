package com.ferfig.bakingapp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailStepAdapter extends RecyclerView.Adapter<RecipeDetailStepAdapter.StepViewHolder>{
    private final Context mContext;

    private final List<Step> mData;

    private final int mSelectedStep;

    private List<CardView> mAllCardViewItems = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Step stepData);
    }
    private final OnItemClickListener itemClickListener;

    public RecipeDetailStepAdapter(Context mContext, List<Step> mData, int selectedItem, OnItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mSelectedStep = selectedItem;
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
        holder.bind(mData.get(position), position, this.itemClickListener);
        mAllCardViewItems.add(holder.cardViewItem);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvStepName)
        TextView tvStepName;

        @BindView(R.id.step_item_card_view)
        CardView cardViewItem;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Step stepData, final int position, final OnItemClickListener listener) {
            String stepName = stepData.getShortDescription();
            tvStepName.setText(stepName);

            if (position == mSelectedStep){
                cardViewItem.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorCardViewSelected));
            }else{
                cardViewItem.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorCardViewBackground));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener!=null) {
                        listener.onItemClick(stepData);
                        for(CardView cardView : mAllCardViewItems){
                            if (cardViewItem.equals(cardView)) {
                                cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorCardViewSelected));
                            } else {
                                cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorCardViewBackground));
                            }
                        }
                    }
                }
            });
        }
    }
}
