package com.darkwolve.khome.storygame.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.darkwolve.khome.storygame.Activity.ChatActivity;
import com.darkwolve.khome.storygame.ChatClasses.MessageWrapper;
import com.darkwolve.khome.storygame.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by khome on 26/3/17.
 */

public class SuggestionDialogFragment extends DialogFragment {

    ArrayList<MessageWrapper> radioList;
    int selectedRadio=-1;
    public void setRadioOption(ArrayList<MessageWrapper> m)
    {
        radioList=m;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View v=inflater.inflate(R.layout.suggestion_layout, container, false);
        RadioGroup rg= ButterKnife.findById(v,R.id.radiogroup);
        RadioButton rb0=ButterKnife.findById(v,R.id.radio_suggestion0);
        RadioButton rb1=ButterKnife.findById(v,R.id.radio_suggestion1);
        RadioButton rb2=ButterKnife.findById(v,R.id.radio_suggestion2);
        rb0.setText(radioList.get(0).getSubText());
        rb1.setText(radioList.get(1).getSubText());
        rb2.setText(radioList.get(2).getSubText());
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i)
                {
                    case R.id.radio_suggestion0:

                        selectedRadio=0;
                        break;
                    case R.id.radio_suggestion1:

                        selectedRadio=1;
                        break;
                    case R.id.radio_suggestion2:

                        selectedRadio=2;
                        break;

                }
                ChatActivity callingActivity = (ChatActivity) getActivity();
                callingActivity.dialogDismiss(selectedRadio);

            }
        });


        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
   /* @Override
    public void onDismiss(final DialogInterface dialog) {
        //Fragment dialog had been dismissed
        if(selectedRadio==-1)
        {
            ChatActivity callingActivity = (ChatActivity) getActivity();
            callingActivity.dialogCancel();

        }
    }*/

    /*@Override
    public void onCancel(DialogInterface dialog) {
        ChatActivity callingActivity = (ChatActivity) getActivity();
        callingActivity.dialogCancel();
    }*/


}