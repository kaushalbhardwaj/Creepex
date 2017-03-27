package com.example.khome.storygame.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.example.khome.storygame.ChatClasses.MessageWrapper;
import com.example.khome.storygame.ChatClasses.Suggestion;
import com.example.khome.storygame.Fragment.SuggestionDialogFragment;
import com.example.khome.storygame.Handler.RetrievalDatabaseHandler;
import com.example.khome.storygame.R;
import com.example.khome.storygame.Tools.SharedPreference;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.messagesList)
    MessagesList messagesList;
    @BindView(R.id.input)
    MessageInput inputView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.typingDisplay)
    LinearLayout typingLayout;
    /*@BindView(R.id.suggestion_list)
    RecyclerView suggestionView;
    @BindView(R.id.suggestionDisplay)
    LinearLayout suggestionLayout;*/

    static String IC_REACHED="file:///android_asset/ic_reached.png";
    static String IC_SENT="file:///android_asset/ic_sent.png";
    static String IC_AI="file:///android_asset/ic_user_man.png";
    static  int DELAY_REACHED=1500;
    static  int DELAY_READ=1500;
    ArrayList<Message> m;
    Message oStatusMessage=null;
    List<Suggestion> listSuggestion=new ArrayList<Suggestion>();
    SuggestionAdapter mSuggestionAdapter;
    int f;
    boolean suggestionshow=true;
    boolean submitClicked=false;
    MessagesListAdapter<Message> adapter;
    TextWatcher textListener = null;
    EditText inputEdit;
    ImageButton inputButton;
    boolean permitTyping=false;
    boolean permitSuggestion=false;
    EditText et;

    String typingText="Suggestion from AI";
    RetrievalDatabaseHandler myDatabase;
    Message currentMessageWrapper;
    SuggestionDialogFragment newFragment;
    ArrayList<MessageWrapper> universalSuggestion;
    ArrayList<Message> historyList;
    int selectedIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);



        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("KDPGame");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getPermission();
        setUpSharedPreference();
        setUpListener();
        //setUpGlobalParameters();
        if(SharedPreference.getCurrentPosition(getApplicationContext())==1)
        {
            startIntroductoryDialog();

        }
        else {
            setMessageList();

        }
        //setUpSuggestion();




        int permissionCheck = ContextCompat.checkSelfPermission(ChatActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck==PackageManager.PERMISSION_GRANTED) {
            /*try {
            Message m = new Message();
            m.setId("3");
            m.setText("hi hw r u");
            Author a=new Author();
                a.setId("2");
                m.setCreatedAt(new Date());
                m.setAuthor(a);


                //MessageWrapper mlist = myDatabase.getMessageNode(1);

                //ArrayList<MessageWrapper> mw=myDatabase.getNextMessageNodes("5|6|7");

                MessageWrapper mw=new MessageWrapper();
                mw.setType("bot");
                mw.setMessage(m);
                myDatabase.putHistoryNode(mw);
                //Log.e("database", mlist.getNextNode() + " "+mlist.getSubText());
                //Log.e("database2", mw.size() + " ");
            } catch (Exception e) {


            }
*/
        }
        else
        {
            Toast.makeText(this, "N0 Permission", Toast.LENGTH_SHORT).show();

        }


    }

    private void showFirstTime() {

        try {
            String s="bot";
            MessageWrapper m = myDatabase.getMessageNode(1);
            if(m.getType().equals(s))
            {

                oStatusMessage=null;
                adapter = new MessagesListAdapter<>("1",imageLoader);
                messagesList.setAdapter(adapter);
                startBotTyping(m);
            }


        }
        catch (Exception e)
        {

        }

        //permitSuggestion=true;
    }

    private void setUpParameters(MessageWrapper m) {


        universalSuggestion=myDatabase.getNextMessageNodes(m.getNextNode());
        String s2="bot";

        if(universalSuggestion.get(0).getType().equals(s2))
        {
            startBotTyping(universalSuggestion.get(0));

        }
        else
        {
            for (int i=0;i<universalSuggestion.size();i++)
            {
                Date d=new Date();
                    universalSuggestion.get(i).getMessage().getUser().setAvatar(IC_SENT);
                    universalSuggestion.get(i).getMessage().getUser().setName("kaushal");
                    universalSuggestion.get(i).getMessage().setCreatedAt(d);



            }

            permitSuggestion=true;


        }



    }

    private void setUpSharedPreference() {

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);

        if (isFirstRun)
        {
            SharedPreference.putCurrentPosition(getApplicationContext(),1);
            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
        }


    }





    private void setUpListener() {

        f=0;
        suggestionshow=true;
        permitTyping=false;
        myDatabase = new RetrievalDatabaseHandler(ChatActivity.this);
        universalSuggestion=new ArrayList<MessageWrapper>();



        inputEdit=inputView.getInputEditText();
        inputButton=inputView.getButton();
        textListener = new TextWatcher() {
            public void afterTextChanged(Editable s){

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


            }
        };

        inputEdit.addTextChangedListener(textListener);

        inputEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                submitClicked=false;


                if(permitSuggestion) {

                    if(universalSuggestion.size()>1) {
                        if (suggestionshow) {
                            showSuggestionDialog();
                            et=inputView.getInputEditText();
                            //typingLayout.setVisibility(View.VISIBLE);

                            //openKeyboard(inputView.getInputEditText());
                        }
                    }
                    else
                    {
                        typingText=universalSuggestion.get(0).getMessage().getText();
                        suggestionshow=true;
                        openKeyboard(inputView.getInputEditText());
                    }
                }
                return true;
            }


        });


        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {

                permitSuggestion=false;
                permitTyping=false;
                typingLayout.setVisibility(View.INVISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0);
                inputEdit.setText("");
                //typingLayout.setVisibility(View.GONE);
                submitClicked=true;
                f=0;

                Message m1=new Message();
                m1.setCreatedAt(universalSuggestion.get(selectedIndex).getMessage().getCreatedAt());
                m1.setId(universalSuggestion.get(selectedIndex).getMessage().getId());
                m1.setText(universalSuggestion.get(selectedIndex).getMessage().getText());
                Author a=new Author();
                a.setAvatar(IC_SENT);
                a.setId(universalSuggestion.get(selectedIndex).getMessage().getUser().getId());
                a.setName(universalSuggestion.get(selectedIndex).getMessage().getUser().getName());
                m1.setAuthor(a);

                MessageWrapper mw=new MessageWrapper();
                mw.setMessage(m1);
                mw.setType("user");

                adapter.addToStart(m1, true);
                myDatabase.putHistoryNode(mw);

                SharedPreference.putCurrentPosition(getApplicationContext(),Integer.parseInt(m1.getId()));

                Message m2=new Message();
                m2.setCreatedAt(universalSuggestion.get(selectedIndex).getMessage().getCreatedAt());
                m2.setId(universalSuggestion.get(selectedIndex).getMessage().getId());
                m2.setText(universalSuggestion.get(selectedIndex).getMessage().getText());
                Author a2=new Author();
                a2.setAvatar(IC_SENT);
                a2.setId(universalSuggestion.get(selectedIndex).getMessage().getUser().getId());
                a2.setName(universalSuggestion.get(selectedIndex).getMessage().getUser().getName());
                m2.setAuthor(a2);

                try {
                    if(oStatusMessage==null) {

                    }
                    else
                    {
                        oStatusMessage.getUser().setAvatar(null);
                        adapter.updateElement(oStatusMessage, 2);


                    }
                    oStatusMessage=m2;
                   changeReachedStatus();

                }
                catch (Exception e)
                {

                }


                return true;
            }
        });

        inputButton.setEnabled(false);



    }

    private void showSuggestionDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
         newFragment = new SuggestionDialogFragment();


        newFragment.setRadioOption(universalSuggestion);
        newFragment.show(fragmentManager, "dialog");

        suggestionshow=false;


    }

    public void dialogDismiss(int i)
    {

        selectedIndex=i;
        newFragment.dismiss();
        typingText=universalSuggestion.get(i).getMessage().getText();
        suggestionshow=true;
        typingLayout.setVisibility(View.VISIBLE);
        openKeyboard(inputView.getInputEditText());
        //inputEdit.onTouchEvent(null);
        //Toast.makeText(this, "Suggestion Selected "+i, Toast.LENGTH_SHORT).show();
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
                setUpParameters(universalSuggestion.get(selectedIndex));
                //showSuggestion();
            }
        }, DELAY_READ);

    }

    public void showSuggestion()
    {


    }
    public void openKeyboard(EditText et)
    {

        permitTyping=true;
        permitSuggestion=false;

        KeyListener originalKeyListener = et.getKeyListener();
        et.setKeyListener(originalKeyListener);
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


    }

    public void startBotTyping(MessageWrapper m)
    {
        Message m1=new Message();
        Date date = new Date();

        m1.setId(m.getMessage().getId());
        m1.setText(m.getMessage().getText());
        m1.setCreatedAt(date);

        Author a= new Author();
        a.setId(m.getMessage().getUser().getId()+"");
        a.setName(" kak");
        a.setAvatar(IC_AI);
        m1.setAuthor(a);
        MessageWrapper mw=new MessageWrapper();
        mw.setMessage(m1);
        mw.setType("bot");

        adapter.addToStart(m1,true);
        myDatabase.putHistoryNode(mw);

        SharedPreference.putCurrentPosition(getApplicationContext(),Integer.parseInt(m1.getId()));
        //setMessageList();
        setUpParameters(m);

    }

    private void setMessageList() {


        oStatusMessage=null;
        adapter = new MessagesListAdapter<>("1",imageLoader);
        messagesList.setAdapter(adapter);

        getHistoryList();

        adapter.addToEnd(historyList, true);

        MessageWrapper mw = myDatabase.getMessageNode(SharedPreference.getCurrentPosition(getApplicationContext()));

        setUpParameters(mw);



        //setUpParameters();

       /* m=new ArrayList<Message>();

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
        adapter.addToStart(m2,true);*/

        //oStatusMessage=m2;

    }

    private void getHistoryList() {
        historyList=myDatabase.getHistoryMessages();

        String s="1";
        for(int i=0;i<historyList.size();i++)
        {
            if(historyList.get(i).getUser().getId().equals("1"))
            {


                historyList.get(i).getUser().setAvatar(null);
                historyList.get(i).getUser().setName("asdf");
            }

            else {

                historyList.get(i).getUser().setAvatar(IC_AI);
                historyList.get(i).getUser().setName("bot");

            }

        }


    }

    private void startIntroductoryDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
// Add the buttons

        builder.setMessage("Welcome this is a story game")
                .setTitle("Story Game");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                showFirstTime();

            }
        });



        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
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

    private void getPermission() {

        if (ContextCompat.checkSelfPermission(ChatActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(ChatActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
