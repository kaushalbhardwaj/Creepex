package com.darkwolve.khome.storygame.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.darkwolve.khome.storygame.Activity.ChatActivity;
import com.darkwolve.khome.storygame.ChatClasses.MessageWrapper;
import com.darkwolve.khome.storygame.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by khome on 28/3/17.
 */

public class PlayerNameDialogFragment extends DialogFragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View v=inflater.inflate(R.layout.dialog_signin, container, false);

        Button bt=ButterKnife.findById(v,R.id.button);
        final EditText et=ButterKnife.findById(v,R.id.nickname);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity callingActivity = (ChatActivity) getActivity();
                callingActivity.showFirstTime(et.getText().toString());

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


}