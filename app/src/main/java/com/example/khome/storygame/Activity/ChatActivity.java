package com.example.khome.storygame.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.khome.storygame.ChatClasses.Author;
import com.example.khome.storygame.ChatClasses.Message;
import com.example.khome.storygame.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.input;
import static android.R.id.message;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.messagesList)
    MessagesList messagesList;
    @BindView(R.id.input)
    MessageInput inputView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.typingDisplay)
    LinearLayout typingLayout;
    @BindView(R.id.status)
    ImageView messageStatus;

    List<Message> m;
    int f;
    boolean submitClicked=false;
    MessagesListAdapter<Message> adapter;
    TextWatcher textListener = null;
    EditText inputEdit;
    ImageButton inputButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        /*Intent i=new Intent(ChatActivity.this,KeyboardActivity.class);
        startActivity(i);*/

        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("KDPGame");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpListener();

        setMessageList();



    }

    private void setUpListener() {

        final String s1="Hi,who are u I am very Hungry";
        f=0;

        inputEdit=inputView.getInputEditText();
        inputButton=inputView.getButton();


        textListener = new TextWatcher() {
            public void afterTextChanged(Editable s){
                //inputEdit.setSelection(2);
                /*if(f<s1.length())
                {
                    inputEdit.setSelection(f+1);
                }
                else
                {
                    inputEdit.setSelection(s1.length());

                }*/
            }
            public void beforeTextChanged(CharSequence s,int start,int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputEdit.removeTextChangedListener(textListener);
                if(submitClicked==true)
                {
                    inputButton.setEnabled(false);
                    submitClicked=false;

                }
                else if(f<s1.length()) {
                    inputEdit.setText(s1.subSequence(0, f));
                    inputEdit.setSelection(f);
                    inputButton.setEnabled(false);

                }
                else
                {
                    inputButton.setEnabled(true);
                    inputEdit.setText(s1);
                    inputEdit.setSelection(s1.length());
                }
                    inputEdit.addTextChangedListener(textListener);
                f++;
                if(f==s1.length())
                    inputButton.setEnabled(true);


                try {

                    /*MediaPlayer mediaPlayer = MediaPlayer.create(ChatActivity.this, R.raw.sound);
                    mediaPlayer.start();*/
                /*    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
*/

                    /*Uri path = Uri.parse("android.resource://"+getPackageName()+"/raw/sound3.mp3");
                    RingtoneManager.setActualDefaultRingtoneUri(
                            getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                            path);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), path);
                    r.play();
*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        inputEdit.addTextChangedListener(textListener);

        inputEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Toast.makeText(ChatActivity.this, "Hikasdf", Toast.LENGTH_SHORT).show();


                submitClicked=false;
                typingLayout.setVisibility(View.VISIBLE);
                messageStatus.setVisibility(View.GONE);
                openKeyboard(inputView.getInputEditText());
                return true;
            }


        });


        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //Toast.makeText(ChatActivity.this, ""+inputEdit.getText(), Toast.LENGTH_SHORT).show();


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0);
                inputEdit.setText("");
                typingLayout.setVisibility(View.GONE);
                submitClicked=true;
                f=0;


                Date d=new Date();
                Message m=new Message();
                m.setText(input+"");
                m.setCreatedAt(d);

                Author a= new Author();
                a.setId("1");
                a.setName("Kaushal");
                a.setAvatar(null);
                m.setAuthor(a);


                adapter.addToStart(m, true);

                messageStatus.setVisibility(View.VISIBLE);
                messageStatus.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,R.drawable.ic_reached));

                messageStatus.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,R.drawable.ic_read));


                return true;
            }
        });


    }
    public void openKeyboard(EditText et)
    {

        KeyListener originalKeyListener = et.getKeyListener();
        et.setKeyListener(originalKeyListener);
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);


    }

    private void setMessageList() {

        m=new ArrayList<Message>();

        adapter = new MessagesListAdapter<>("1",imageLoader);
        Date date = new Date();

        Message m1=new Message();
        m1.setId("2");
        m1.setText("Hi hw r u??");
        m1.setCreatedAt(date);

        Author a= new Author();
        a.setId("2");
        a.setName("Kaushal");
        a.setAvatar("file:///android_asset/ic_user_man.png");
        m1.setAuthor(a);

        m.add(m1);
        m.add(m1);



        messagesList.setAdapter(adapter);


        adapter.addToEnd(m, true);

        Message m2=new Message();
        Date d2=new Date();
        m2.setCreatedAt(d2);
        m2.setText("who are u?? who are u?? who are u??  who are u?? who are u?? who are u?? who are u??  who are u??  ");
        m2.setId("1");
        Author a2= new Author();
        a2.setId("1");
        a2.setName("kaushal");
        a2.setAvatar(null);
        m2.setAuthor(a2);
        adapter.addToStart(m2,true);

    }

    ImageLoader imageLoader = new ImageLoader() {
        @Override
        public void loadImage(ImageView imageView, String url) {
            Picasso.with(ChatActivity.this).load(url).into(imageView);
        }
    };



}
