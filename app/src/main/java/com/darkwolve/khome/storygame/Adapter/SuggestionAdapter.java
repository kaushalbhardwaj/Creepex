package com.darkwolve.khome.storygame.Adapter;

import android.support.annotation.Keep;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darkwolve.khome.storygame.ChatClasses.Suggestion;

import java.util.List;

import proguard.annotation.KeepClassMembers;

/**
 * Created by khome on 17/3/17.
 */

@Keep
@KeepClassMembers
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.MyViewHolder> {

    private List<Suggestion> suggestionList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView suggestion ;

        public MyViewHolder(View view) {
            super(view);
            //suggestion = (TextView) view.findViewById(R.id.suggestionText);
        }
    }


    public SuggestionAdapter(List<Suggestion> suggestionList) {
        this.suggestionList = suggestionList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null; //take care of this

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Suggestion suggestionText = suggestionList.get(position);
        holder.suggestion.setText(suggestionText.getText());

    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }
}