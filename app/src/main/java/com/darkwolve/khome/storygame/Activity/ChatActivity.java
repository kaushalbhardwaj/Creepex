package com.darkwolve.khome.storygame.Activity;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.darkwolve.khome.storygame.ChatClasses.Author;
import com.darkwolve.khome.storygame.ChatClasses.Message;
import com.darkwolve.khome.storygame.ChatClasses.MessageWrapper;
import com.darkwolve.khome.storygame.ChatClasses.Suggestion;
import com.darkwolve.khome.storygame.Fragment.PlayerNameDialogFragment;
import com.darkwolve.khome.storygame.Fragment.SuggestionDialogFragment;
import com.darkwolve.khome.storygame.Handler.RetrievalDatabaseHandler;
import com.darkwolve.khome.storygame.R;
import com.darkwolve.khome.storygame.Tools.SharedPreference;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    int f;
    boolean suggestionshow=true;
    boolean submitClicked=false;
    MessagesListAdapter<Message> adapter;
    TextWatcher textListener = null;
    EditText inputEdit;
    ImageButton inputButton;
    boolean permitTyping=false;
    boolean permitSuggestion=false;
    ProgressDialog pd;
    EditText et;

    String typingText="Suggestion from AI";
    RetrievalDatabaseHandler myDatabase;
    Message currentMessageWrapper;
    SuggestionDialogFragment newFragment;
    PlayerNameDialogFragment pdf;
    ArrayList<MessageWrapper> universalSuggestion;
    ArrayList<Message> historyList;
    int selectedIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        catch (Exception e)
        {


        }
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);



        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("KDPGame");

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_man_user);



        //getPermission();
        setUpSharedPreference();
        setUpListener();
        //setUpGlobalParameters();

        //startIntroductoryDialog();


        if(SharedPreference.getCurrentPosition(getApplicationContext())==1)
        {
            startIntroductoryDialog();

        }
        else {

                    pd=new ProgressDialog(ChatActivity.this);
                    pd.setTitle("");
                    pd.setMessage("");
                    pd.setCancelable(false);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.show();



            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    setMessageList();
                    pd.dismiss();

                }
            }, 3000);




            //setMessageList();

        }



    }

    public void showFirstTime(String name) {

        try {
            pdf.dismiss();
            SharedPreference.putPlayerName(getApplicationContext(),name);
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

    private void setUpParameters(final MessageWrapper m) {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                universalSuggestion=myDatabase.getNextMessageNodes(m.getNextNode());
                String s2="bot";
                String s3="end";

                if(universalSuggestion.get(0).getType().equals(s3))
                {
                    showLastTime(universalSuggestion.get(0));

                }

                else if(universalSuggestion.get(0).getType().equals(s2))
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
        }, 2000);





    }

    private void showLastTime(MessageWrapper m) {


        Message m1=new Message();
        Date date = new Date();

        m1.setId(m.getMessage().getId());
        //m1.setText(m.getMessage().getText());
        m1.setText(m.getMessage().getText());
        m1.setCreatedAt(date);

        Author a= new Author();
        a.setId(m.getMessage().getUser().getId()+"");
        a.setName("Annie96");
        a.setAvatar(IC_AI);
        m1.setAuthor(a);
        MessageWrapper mw=new MessageWrapper();
        mw.setMessage(m1);
        mw.setType("bot");

        adapter.addToStart(m1,true);
        myDatabase.putHistoryNode(mw);

        SharedPreference.putCurrentPosition(getApplicationContext(),1);

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
// Add the buttons

        builder.setMessage("Thank you for playing the game hope you have enjoyed the game")
                .setTitle("annie96 is typing...");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                finish();
            }
        });




        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();


    }

    private void setUpSharedPreference() {

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);

        if (isFirstRun)
        {
            SharedPreference.putCurrentPosition(getApplicationContext(),1);
            SharedPreference.putPlayerName(getApplicationContext(),"David");
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




       /* OverScrollDecoratorHelper.setUpOverScroll(messagesList, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
// Vertical
        OverScrollDecoratorHelper.setUpOverScroll(messagesList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);*/
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
                //playSound(R.raw.sound_reached);
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

                if(permitTyping)
                {
                    openKeyboard(inputView.getInputEditText());

                }


                else if(permitSuggestion) {

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
                //typingLayout.setVisibility(View.INVISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0);
                inputEdit.setText("");
                //typingLayout.setVisibility(View.GONE);
                submitClicked=true;
                f=0;

                Message m1=new Message();
                m1.setCreatedAt(universalSuggestion.get(selectedIndex).getMessage().getCreatedAt());
                m1.setId(universalSuggestion.get(selectedIndex).getMessage().getId());
                //m1.setText(universalSuggestion.get(selectedIndex).getMessage().getText());
                m1.setText("This is a test message for checking the ui");
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
                //m2.setText(universalSuggestion.get(selectedIndex).getMessage().getText());
                m2.setText("This is a test message for checking the ui");

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
                    playSound(R.raw.sound_sent);

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
       /* newFragment.getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogCancel();
            }
        });*/

        newFragment.setCancelable(false);
        newFragment.show(fragmentManager, "dialog");

        suggestionshow=false;


    }

    public void dialogDismiss(int i)
    {

        selectedIndex=i;
        newFragment.dismiss();
        typingText=universalSuggestion.get(i).getMessage().getText();
        suggestionshow=true;
       // typingLayout.setVisibility(View.VISIBLE);
        openKeyboard(inputView.getInputEditText());
        //inputEdit.onTouchEvent(null);
        //Toast.makeText(this, "Suggestion Selected "+i, Toast.LENGTH_SHORT).show();
    }

    public void dialogCancel()
    {
        permitSuggestion=true;
        suggestionshow=false;
    }

    public void changeReachedStatus()
    {


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                oStatusMessage.getUser().setAvatar(IC_REACHED);
                adapter.updateElement(oStatusMessage,0);
                changeReadStatus();
            }
        }, 2000);

    }
    public void changeReadStatus()
    {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                oStatusMessage.getUser().setAvatar(IC_AI);
                adapter.updateElement(oStatusMessage,0);
                playSound(R.raw.sound_read);
                setUpParameters(universalSuggestion.get(selectedIndex));
                //showSuggestion();
            }
        }, 2000);

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

    public void startBotTyping(final MessageWrapper m)
    {
        final Message m1=new Message();
        Date date = new Date();

        m1.setId(m.getMessage().getId());
        //m1.setText(m.getMessage().getText());
        m1.setText("This is a test message for checking the ui");
        m1.setCreatedAt(date);

        Author a= new Author();
        a.setId(m.getMessage().getUser().getId()+"");
        a.setName("Annie96");
        a.setAvatar(IC_AI);
        m1.setAuthor(a);
        final MessageWrapper mw=new MessageWrapper();
        mw.setMessage(m1);
        mw.setType("bot");

        typingLayout.setVisibility(View.VISIBLE);
        playSound(R.raw.sound_typing);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


                typingLayout.setVisibility(View.GONE);
                adapter.addToStart(m1,true);
                myDatabase.putHistoryNode(mw);

                SharedPreference.putCurrentPosition(getApplicationContext(),Integer.parseInt(m1.getId()));
                //setMessageList();
                setUpParameters(m);

            }
        }, 3000);



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

        builder.setMessage("The creepiest chat you'll ever do.")
                .setTitle("annie96 is typing...");
        builder.setPositiveButton("PLAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                 pdf = new PlayerNameDialogFragment();

                pdf.setCancelable(false);
                pdf.show(fragmentManager, "dialog");

                //showFirstTime();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                finish();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try {

                    /*Intent i=new Intent(ChatActivity.this,StorySelectionActivity.class);
                    startActivity(i);
                    finish();*/
                }
                catch (Exception e)
                {


                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
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
