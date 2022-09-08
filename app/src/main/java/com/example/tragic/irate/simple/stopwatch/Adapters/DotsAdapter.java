package com.example.tragic.irate.simple.stopwatch.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragic.irate.simple.stopwatch.R;

import java.util.ArrayList;
import java.util.List;

public class DotsAdapter extends RecyclerView.Adapter<DotsAdapter.DotsViewHolder> {
    Context mContext;
    List<String> mRoundList;
    List<Integer> mCharactersInRoundList;
    Typeface narrowFont;
    Typeface narrowFontBold;
    Typeface bigShouldersFont;

    public DotsAdapter(Context context, List<String> roundList) {
        this.mContext = context; this.mRoundList = roundList;
        instantiateMiscObjects();
    }

    public void setRoundList(List<String> roundList) {
        this.mRoundList = roundList;
        setCharactersInRoundList();
        Log.i("testRound", "list is " + mCharactersInRoundList);
    }

    private void setCharactersInRoundList() {
        mCharactersInRoundList = new ArrayList<>();

        for (int i=0; i<mRoundList.size(); i++) {
            mCharactersInRoundList.add(mRoundList.get(i).length());
        }
    }

    private float textSizeForEachRound(int numberOfRoundChars) {
        return 40 - (5* (numberOfRoundChars-1));
    }

    @NonNull
    @Override
    public DotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dots_recycler_views, parent, false);

        return new DotsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DotsViewHolder holder, int position) {
        holder.roundText.setText(mRoundList.get(position));
        holder.roundText.setTextSize(textSizeForEachRound(mCharactersInRoundList.get(position)));

        if (mCharactersInRoundList.get(position) >=4) {
            holder.roundText.setTypeface(bigShouldersFont);
        }
    }

    @Override
    public int getItemCount() {
        return mRoundList.size();
    }

    public class DotsViewHolder extends RecyclerView.ViewHolder {
        public TextView roundText;

        public DotsViewHolder(@NonNull View itemView) {
            super(itemView);
            roundText = itemView.findViewById(R.id.round_string_textView);
        }
    }

    private void instantiateMiscObjects() {
        narrowFont = ResourcesCompat.getFont(mContext, R.font.archivo_narrow);
        narrowFontBold = ResourcesCompat.getFont(mContext, R.font.archivo_narrow_bold);
        bigShouldersFont = ResourcesCompat.getFont(mContext, R.font.big_shoulders_text_bold);
    }
}