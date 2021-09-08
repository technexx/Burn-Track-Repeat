package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//Todo: Callbacks.
public class SavedPomCycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<String> mPomList;
    ArrayList<String> mPomTitle;
    onCycleClickListener mOnCycleClickListener;
    onHighlightListener mOnHighlightListener;
    boolean mHighlightDeleted;
    boolean mHighlightMode;
    List<String> mPositionList;
    ArrayList<Integer> mSizeToggle = new ArrayList<>();

    public interface onCycleClickListener {
        void onCycleClick (int position);
    }

    public interface onHighlightListener {
        void onCycleHighlight (List<String> listOfPositions, boolean addButtons);
    }

    public void setItemClick(onCycleClickListener xOnCycleClickListener) {
        this.mOnCycleClickListener = xOnCycleClickListener;
    }

    public void setHighlight(onHighlightListener xOnHighlightListener) {
        this.mOnHighlightListener = xOnHighlightListener;
    }

    public SavedPomCycleAdapter(Context context, ArrayList<String> pomList, ArrayList<String> pomTitle) {
        this.mContext = context; this.mPomList = pomList; this.mPomTitle = pomTitle;
        //Populates a toggle list for Pom's spannable colors so we can simply replace them at will w/ out resetting the list. This should only be called in our initial adapter instantiation.
        if (mSizeToggle.size()==0) for (int i=0; i<8; i++) mSizeToggle.add(0);
        //Must be instantiated here so it does not loop and reset in onBindView.
        mPositionList = new ArrayList<>();
    }


    public void removeHighlight(boolean cancelMode) {
        //If boolean is false, highlight has simply been deleted and we clear the highlight list while turning all backgrounds black.
        mHighlightDeleted = true;
        //If boolean is true, we have canceled the highlight process entirely, which does the above but also removes the Trash/Back buttons (done in Main) and sets the next row click to launch a timer instead of highlight (done here).
        if (cancelMode) mHighlightMode = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.mode_three_cycles, parent, false);
        return new PomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PomHolder pomHolder = (PomHolder) holder;

        pomHolder.pomName.setText(mPomTitle.get(position));
        String tempPom = (convertTime(mPomList).get(position));
        tempPom = tempPom.replace("-", mContext.getString(R.string.bullet));
        Spannable pomSpan = new SpannableString(tempPom);

        //Sets green/red alternating colors using text char indices.
        int moving = 0;
        int rangeStart = 0;
        int rangeEnd = 4;
        for (int i=0; i<8; i++) {
            if (mSizeToggle.get(i)==1) rangeEnd = 5;
            if (i%2==0) pomSpan.setSpan(new ForegroundColorSpan(Color.GREEN), moving + rangeStart, moving + rangeEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            else pomSpan.setSpan(new ForegroundColorSpan(Color.RED), moving + rangeStart, moving + rangeEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            if (mSizeToggle.get(i)==1) moving+=8; else moving+=7;
        }
        pomHolder.pomView.setText(pomSpan);

        if (mHighlightDeleted) {
            //Clears highlight list.
            mPositionList.clear();
            //Turns our highlight mode off so single clicks launch a cycle instead of highlight it for deletion.
            mHighlightMode = false;
            //Sets all of our backgrounds to black (unhighlighted).
            for (int i=0; i<mPomList.size(); i++) {
                pomHolder.fullView.setBackgroundColor(Color.BLACK);
            }
        }

        pomHolder.fullView.setOnClickListener(v-> {
            boolean changed = false;
            if (!mHighlightMode) mOnCycleClickListener.onCycleClick(position); else {
                ArrayList<String> tempList = new ArrayList<>(mPositionList);

                //Iterate through every cycle in list.
                for (int i=0; i<mPomList.size(); i++) {
                    //Using tempList for stable loop since mPositionList changes.
                    for (int j=0; j<tempList.size(); j++) {
                        //If our cycle position matches a value in our "highlighted positions list", we un-highlight it, and remove it from our list.
                        if (String.valueOf(position).contains(tempList.get(j))) {
                            pomHolder.fullView.setBackgroundColor(Color.BLACK);
                            mPositionList.remove(String.valueOf(position));
                            //Since we want a single highlight toggle per click, our boolean set to true will preclude the addition of a highlight below.
                            changed = true;
                        }
                    }
                }
                //If we have not toggled our highlight off above, toggle it on below.
                if (!changed) {
                    //Adds the position at its identical index for easy removal access.
                    mPositionList.add(String.valueOf(position));
                    pomHolder.fullView.setBackgroundColor(Color.GRAY);
                }
                //Callback to send position list (Using Strings to make removing values easier) back to Main.
                mOnHighlightListener.onCycleHighlight(mPositionList, false);
            }
        });

        pomHolder.fullView.setOnLongClickListener(v-> {
            if (!mHighlightMode) {
                mPositionList.add(String.valueOf(position));
                pomHolder.fullView.setBackgroundColor(Color.GRAY);
                mHighlightMode = true;
                //Calls back for initial highlighted position, Also to set actionBar views for highlight mode.
                mOnHighlightListener.onCycleHighlight(mPositionList, true);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mPomList.size();
    }

    public class PomHolder extends RecyclerView.ViewHolder {
        public TextView pomName;
        public TextView pomView;
        public View fullView;

        public PomHolder(@NonNull View itemView) {
            super(itemView);
            pomName = itemView.findViewById(R.id.pom_header);
            pomView = itemView.findViewById(R.id.pom_view);
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
                //If in Pom mode, set "0" for a time entry that is <10 minutes/4 length (e.g. X:XX), and "1" for >=10 minutes/5 length (e.g. XX:XX). This is so we can properly alternate green/red coloring in onBindView's Spannable.
                if ((newString.get(k)).length()==4) mSizeToggle.set(k, 0); else mSizeToggle.set(k, 1);
            }
            finalSplit = String.valueOf(newString);
            finalSplit = finalSplit.replace("[", "");
            finalSplit = finalSplit.replace("]", "");
            finalSplit = finalSplit.replace(",", " " + mContext.getString(R.string.bullet));
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
        totalSeconds = totalSeconds/1000;

        if (totalSeconds >=60) {
            minutes = totalSeconds/60;
            remainingSeconds = totalSeconds % 60;
            return (minutes + ":" + df.format(remainingSeconds));
        } else if (totalSeconds >=10) return "0:" + totalSeconds;
        else return "0:0" + totalSeconds;
    }
}
