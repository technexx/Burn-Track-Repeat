package com.example.tragic.irate.simple.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedCycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<String> mSetsList;
    ArrayList<String> mBreaksList;
    ArrayList<String> mBreaksOnlyList;
    ArrayList<String> mTitle;
    ArrayList<String> mBreaksOnlyTitle;
    boolean mBreaksOnly;
    onCycleClickListener mOnCycleClickListener;
    onDeleteCycleListener mOnDeleteCycleListener;
    onEditTitleListener mOnEditTitleListener;
    public static final int SETS_AND_BREAKS = 0;
    public static final int BREAKS_ONLY = 1;

    public interface onCycleClickListener {
        void onCycleClick (int position);
    }

    public interface onDeleteCycleListener {
        void onCycleDelete (int position);
    }

    public interface onEditTitleListener {
        void onTitleEdit (String text, int position);
    }

    public void setItemClick(onCycleClickListener xOnCycleClickListener) {
        this.mOnCycleClickListener = xOnCycleClickListener;
    }

    public void setDeleteCycle(onDeleteCycleListener xOnDeleteCycleListener) {
        this.mOnDeleteCycleListener = xOnDeleteCycleListener;
    }

    public void setTitleEdit(onEditTitleListener xOnEditTitleListener) {
        this.mOnEditTitleListener = xOnEditTitleListener;
    }

    public SavedCycleAdapter (Context context, ArrayList<String> setsList, ArrayList<String> breaksList, ArrayList<String> breaksOnlyList, ArrayList<String> title, ArrayList<String> breaksOnlyTitleArray) {
        this.mContext = context; mSetsList = setsList; mBreaksList = breaksList; mBreaksOnlyList = breaksOnlyList; this.mTitle = title; this.mBreaksOnlyTitle = breaksOnlyTitleArray;
    }

    public void setBreaksOnly(boolean breaksOnly){
        mBreaksOnly = breaksOnly;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == SETS_AND_BREAKS) {
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
            customHolder.customName.setText(mTitle.get(position));
            customHolder.customSet.setText(convertTime(mSetsList).get(position));
            customHolder.customBreak.setText(convertTime(mBreaksList).get(position));

//            customHolder.customName.setOnClickListener(v-> {
//                mOnEditTitleListener.onTitleEdit(customHolder.customName.getText().toString(), position);
//            });

            customHolder.fullView.setOnClickListener(v -> {
                mOnCycleClickListener.onCycleClick(position);
            });
            customHolder.customTrash.setOnClickListener(v-> {
                mOnDeleteCycleListener.onCycleDelete(position);
            });

        } else if (holder instanceof BreaksOnlyHolder) {
            BreaksOnlyHolder breaksOnlyHolder = (BreaksOnlyHolder) holder;
            breaksOnlyHolder.breaksOnlyTitleArray.setText(mBreaksOnlyTitle.get(position));
            breaksOnlyHolder.breaksOnlyBreak.setText(convertTime(mBreaksOnlyList).get(position));

            breaksOnlyHolder.fullView.setOnClickListener(v -> {
                mOnCycleClickListener.onCycleClick(position);
            });
            breaksOnlyHolder.breaksOnlyTrash.setOnClickListener(v-> {
                mOnDeleteCycleListener.onCycleDelete(position);
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!mBreaksOnly) return SETS_AND_BREAKS; else return BREAKS_ONLY;
    }

    @Override
    public int getItemCount() {
        if (!mBreaksOnly && mSetsList.size() > 0) return mSetsList.size();
        else if (mBreaksOnly && mBreaksOnlyList.size() > 0) return mBreaksOnlyList.size();
        else return 0;
    }

    public class CustomHolder extends RecyclerView.ViewHolder {
//        public EditText editName;
        public TextView customName;
        public TextView customSet;
        public TextView customBreak;
        public ImageButton customTrash;
        public View fullView;

        @SuppressLint("ResourceAsColor")
        public CustomHolder(@NonNull View itemView) {
            super(itemView) ;
//            editName = itemView.findViewById(R.id.custom_header_edit);
            customName = itemView.findViewById(R.id.custom_name_header);
            customSet = itemView.findViewById(R.id.saved_custom_set_view);
            customBreak = itemView.findViewById(R.id.saved_custom_break_view);
            customTrash = itemView.findViewById(R.id.delete_cycle);
            fullView = itemView;
        }
    }

    public class BreaksOnlyHolder extends RecyclerView.ViewHolder {
        public TextView breaksOnlyTitleArray;
        public TextView breaksOnlyBreak;
        public ImageButton breaksOnlyTrash;
        public View fullView;

        public BreaksOnlyHolder(@NonNull View itemView) {
            super(itemView);
            breaksOnlyTitleArray = itemView.findViewById(R.id.breaks_only_header);
            breaksOnlyBreak = itemView.findViewById(R.id.saved_breaks_only_view);
            breaksOnlyTrash = itemView.findViewById(R.id.delete_cycle_bo);
            fullView = itemView;
        }
    }

    public ArrayList<String> convertTime(ArrayList<String> time) {
        ArrayList<Long> newLong = new ArrayList<>();
        ArrayList<String> newString = new ArrayList<>();
        String listConv = "";
        String[] newSplit = null;
        String finalSplit = "";
        ArrayList<String> finalList = new ArrayList<>();

        for (int i=0; i<time.size(); i++) {
            //Getting each String entry from String Array.
            listConv = String.valueOf(time.get(i));
            //Splitting into String[] entries.
            newSplit = listConv.split(" - ", 0);

            for (int k=0; k<newSplit.length; k++) {
                //Creating new ArrayList of Long values.
                newLong.add(Long.parseLong(newSplit[k]));
                //Converting each Long value into a String we can display.
                newString.add(convertSeconds(newLong.get(k)));
            }
            finalSplit = String.valueOf(newString);
            finalSplit = finalSplit.replace("[", "");
            finalSplit = finalSplit.replace("]", "");
            finalSplit = finalSplit.replace(",", " " + mContext.getString( R.string.bullet));
            finalList.add(finalSplit);

            newLong = new ArrayList<>();
            newString = new ArrayList<>();
        }
        return finalList;
    }

    public String convertSeconds(long totalSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        long minutes;
        long remainingSeconds;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else if (totalSeconds != 5) return String.valueOf(totalSeconds);
        else return "05";
    }
}