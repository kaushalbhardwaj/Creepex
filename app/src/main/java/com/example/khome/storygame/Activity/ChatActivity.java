package com.example.khome.storygame.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.khome.storygame.Adapter.RecyclerTouchListener;
import com.example.khome.storygame.Adapter.SuggestionAdapter;
import com.example.khome.storygame.ChatClasses.Author;
import com.example.khome.storygame.ChatClasses.Message;
import com.example.khome.storygame.ChatClasses.Suggestion;
import com.example.khome.storygame.R;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.net.URL;
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
    @BindView(R.id.suggestion_list)
    RecyclerView suggestionView;
    @BindView(R.id.suggestionDisplay)
    LinearLayout suggestionLayout;

    static String IC_REACHED="file:///android_asset/ic_reached.png";
    static String IC_SENT="file:///android_asset/ic_sent.png";
    static String IC_AI="file:///android_asset/ic_user_man.png";
    static  int DELAY_REACHED=1500;
    static  int DELAY_READ=1500;
    List<Message> m;
    Message oStatusMessage=null;
    List<Suggestion> listSuggestion=new ArrayList<Suggestion>();
    SuggestionAdapter mSuggestionAdapter;
    int f;
    boolean submitClicked=false;
    MessagesListAdapter<Message> adapter;
    TextWatcher textListener = null;
    EditText inputEdit;
    ImageButton inputButton;
    boolean permitTyping=false;
    String typingText="Suggestion from AI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("KDPGame");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpListener();
        setMessageList();
        setUpSuggestion();




    }

    private void setUpSuggestion() {
        Suggestion s1=new Suggestion();
        s1.setText("suggestion from ai1");
        s1.setId(1);
        listSuggestion.add(s1);
        Suggestion s2=new Suggestion();
        s2.setId(2);
        s2.setText("suggestion from ai");
        listSuggestion.add(s2);
        listSuggestion.add(s2);
        listSuggestion.add(s2);
        mSuggestionAdapter=new SuggestionAdapter(listSuggestion);
        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,true);
        suggestionView.setItemAnimator(new DefaultItemAnimator());
        suggestionView.setLayoutManager(mLayoutManager);
        suggestionView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), suggestionView ,new RecyclerTouchListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                permitTyping=true;
                Suggestion s= listSuggestion.get(position);
                //Toast.makeText(ChatActivity.this, s.getText()+"", Toast.LENGTH_SHORT).show();
                typingLayout.setVisibility(View.VISIBLE);
                openKeyboard(inputView.getInputEditText());
                suggestionLayout.setVisibility(View.GONE);
                typingText=s.getText();

            }

            @Override public void onLongItemClick(View view, int position) {

            }
        }));
        suggestionView.setAdapter(mSuggestionAdapter);
        suggestionView.scrollToPosition(listSuggestion.size()-1);


    }

    private void setUpListener() {

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
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                float vol = 1.0f; //This will be half of the default system sound
                am.playSoundEffect(AudioManager.FX_KEY_CLICK, vol);
                //playSound(R.raw.sound1);
                if(submitClicked==true)
                {
                    inputButton.setEnabled(false);
                    submitClicked=false;

                }
                else if(f<typingText.length()) {
                    inputEdit.setText(typingText.subSequence(0, f));
                    inputEdit.setSelection(f);
                    inputButton.setEnabled(false);

                }
                else
                {
                    inputButton.setEnabled(true);
                    inputEdit.setText(typingText);
                    inputEdit.setSelection(typingText.length());
                }
                    inputEdit.addTextChangedListener(textListener);
                f++;
                if(f==typingText.length())
                    inputButton.setEnabled(true);

                //playSound(R.raw.train);

                //playSound(R.raw.sound1);

            }
        };

        inputEdit.addTextChangedListener(textListener);

        inputEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Toast.makeText(ChatActivity.this, "Hikasdf", Toast.LENGTH_SHORT).show();


                submitClicked=false;
                if(permitTyping) {
                    typingLayout.setVisibility(View.VISIBLE);
                    openKeyboard(inputView.getInputEditText());
                }
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
                a.setAvatar(IC_SENT);
                m.setAuthor(a);


                adapter.addToStart(m, true);
                //playSound(R.raw.sound1);

                try {
                    oStatusMessage.getUser().setAvatar(null);
                    adapter.updateElement(oStatusMessage, 1);
                    oStatusMessage=m;
                   changeReachedStatus();

                }
                catch (Exception e)
                {

                }

                return true;
            }
        });


    }
    public void changeReachedStatus()
    {


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                oStatusMessage.getUser().setAvatar(IC_REACHED);
                adapter.updateElement(oStatusMessage,0);

                playSound(R.raw.sound1);
                changeReadStatus();
            }
        }, DELAY_REACHED);

    }
    public void changeReadStatus()
    {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                oStatusMessage.getUser().setAvatar(IC_AI);
                adapter.updateElement(oStatusMessage,0);
                playSound(R.raw.sound1);
                showSuggestion();
            }
        }, DELAY_READ);

    }

    public void showSuggestion()
    {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                suggestionLayout.setVisibility(View.VISIBLE);
                permitTyping=false;
            }
        }, 1000);


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
        a.setAvatar(IC_AI);
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
        a2.setAvatar(IC_AI);
        m2.setAuthor(a2);
        adapter.addToStart(m2,true);

        oStatusMessage=m2;

    }

    private class ChangeStautsTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {




            return null;

        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }

    public void delayHandle(int time)
    {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                return ;
                }
        }, time);

    }
    public void delay(int time)
    {
        final  int i=time;
        new Thread() {
            public void run() {
                try {
                    sleep(i);
                } catch (Exception e) {
                    e.printStackTrace( );
                }
            }
        }.start();
    }
    ImageLoader imageLoader = new ImageLoader() {
        @Override
        public void loadImage(ImageView imageView, String url) {
            Picasso.with(ChatActivity.this).load(url).into(imageView);
        }
    };

    public void playSound(int a)
    {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(ChatActivity.this, a);
            mediaPlayer.start();
        }
        catch (Exception e)
        {
            Log.e("Media player error",e.toString());
        }
    }

}
