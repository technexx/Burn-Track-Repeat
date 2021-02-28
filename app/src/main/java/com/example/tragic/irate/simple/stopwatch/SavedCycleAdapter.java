package com.example.tragic.irate.simple.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SavedCycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<String> mSetsList;
    ArrayList<String> mBreaksList;
    ArrayList<String> mBreaksOnlyList;
    boolean mBreaksOnly;
    onCycleClickListener mOnCycleClickListener;

    public interface onCycleClickListener {
        void onCycleClick (int position);
    }

    public void setItemClick(onCycleClickListener xOnCycleClickListener) {
        this.mOnCycleClickListener = xOnCycleClickListener;
    }

    public SavedCycleAdapter() {
    }

    public SavedCycleAdapter (Context context, ArrayList<String> setsList, ArrayList<String> breaksList, ArrayList<String> breaksOnlyList, boolean breaksOnly) {
        this.mContext = context; mSetsList = setsList; mBreaksList = breaksList; mBreaksOnlyList = breaksOnlyList; this.mBreaksOnly = breaksOnly;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if(!mBreaksOnly) {
            View view = LayoutInflater.from(context).inflate(R.layout.saved_cycles_views, parent, false);
            return new CustomHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.saved_cycles_breaks_only_views, parent, false);
            return new BreaksOnlyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CustomHolder) {
            CustomHolder customHolder = (CustomHolder) holder;

            customHolder.customSet.setText(mSetsList.get(position));
            customHolder.customBreak.setText(mBreaksList.get(position));

            customHolder.fullView.setOnClickListener(v -> {
                mOnCycleClickListener.onCycleClick(position);
            });

        } else if (holder instanceof BreaksOnlyHolder) {
            BreaksOnlyHolder breaksOnlyHolder = (BreaksOnlyHolder) holder;
            breaksOnlyHolder.breaksOnlyBreak.setText(mBreaksOnlyList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (!mBreaksOnly && mSetsList.size() > 0) return mSetsList.size();
        else if (mBreaksOnly && mBreaksOnlyList.size() > 0) return mBreaksOnlyList.size();
        else return 0;
    }

    public class CustomHolder extends RecyclerView.ViewHolder {
        public TextView customSet;
        public TextView customBreak;
        public View fullView;

        @SuppressLint("ResourceAsColor")
        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            customSet = itemView.findViewById(R.id.saved_custom_set_view);
            customBreak = itemView.findViewById(R.id.saved_custom_break_view);
            fullView = itemView;
        }
    }

    public class BreaksOnlyHolder extends RecyclerView.ViewHolder {
        public TextView breaksOnlyBreak;

        public BreaksOnlyHolder(@NonNull View itemView) {
            super(itemView);
            breaksOnlyBreak = itemView.findViewById(R.id.saved_breaks_only_view);
        }
    }
}