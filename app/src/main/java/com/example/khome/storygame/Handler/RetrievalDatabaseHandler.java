package com.example.khome.storygame.Handler;

/**
 * Created by khome on 25/3/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.khome.storygame.ChatClasses.Author;
import com.example.khome.storygame.ChatClasses.Message;
import com.example.khome.storygame.ChatClasses.MessageWrapper;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Date;

public class RetrievalDatabaseHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "storygame.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static final String ID="id";
    private static final String TEXT="text";
    private static final String AUTHORID="authorid";
    private static final String SUBTEXT="subtext";
    private static final String TYPE="type";
    private static final String NEXTNODE="nextnode";
    private static final String RETRIEVAL_TABLE="chat3";
    private static final String HISTORY_TABLE="history";
    private static final String DATE="date";


    public RetrievalDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public MessageWrapper getMessageNode(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(RETRIEVAL_TABLE, new String[] { ID,
                        TEXT, AUTHORID, SUBTEXT, TYPE, NEXTNODE  }, ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MessageWrapper msg = new MessageWrapper();
        Message m=new Message();
        Author a=new Author();
        m.setId(cursor.getString(0)+"");
        Log.e("database",cursor.getString(0)+" "+cursor.getString(1)+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5));
        m.setText(cursor.getString(1));
        a.setId(cursor.getString(2));
        m.setAuthor(a);
        msg.setSubText(cursor.getString(3));
        msg.setType(cursor.getString(4));
        msg.setNextNode(cursor.getString(5));
        msg.setMessage(m);
        Log.e("database2",m.getText()+m.getUser().getId()+m.getId());
        return msg;
    }

    public ArrayList<MessageWrapper> getNextMessageNodes(String next)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<MessageWrapper> nextList=new ArrayList<MessageWrapper>();
        String s1="\\|";
        String s[]=next.split(s1);
        int a[]={Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2])};

        for(int i=0;i<a.length;i++) {
            Cursor cursor = db.query(RETRIEVAL_TABLE, new String[] { ID,
                            TEXT, AUTHORID, SUBTEXT, TYPE, NEXTNODE  }, ID + "=?",
                    new String[] { String.valueOf(a[i]) }, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();

            MessageWrapper msg = new MessageWrapper();
            Message m=new Message();
            Author a1=new Author();
            m.setId(cursor.getString(0));
            m.setText(cursor.getString(1));
            a1.setId(cursor.getString(2));
            m.setAuthor(a1);
            msg.setSubText(cursor.getString(3));
            msg.setType(cursor.getString(4));
            msg.setNextNode(cursor.getString(5));
            msg.setMessage(m);
            nextList.add(msg);
        }
        return nextList;
    }

    public void putHistoryNode(MessageWrapper mw)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(ID, Integer.parseInt(mw.getMessage().getId()));
        values.put(TEXT,mw.getMessage().getText());
        values.put(AUTHORID,mw.getMessage().getUser().getId());
        values.put(TYPE,mw.getType());
        values.put(DATE,mw.getMessage().getCreatedAt().toString());


        // Inserting Row
        db.insert(HISTORY_TABLE, null, values);
        Log.e("history database","inserted");
        db.close();
    }

    public ArrayList<Message> getHistoryMessages()
    {
        SQLiteDatabase db=getWritableDatabase();
        String[] columns={ID, TEXT,AUTHORID,TYPE,DATE};
        Cursor cursor=db.query(RetrievalDatabaseHandler.HISTORY_TABLE, columns, null, null, null, null, null);
        ArrayList<Message> historyList=new ArrayList<Message>();

        while(cursor.moveToNext()){
            Message m=new Message();
            Author a1=new Author();
            m.setId(cursor.getString(0));
            m.setText(cursor.getString(1));
            a1.setId(cursor.getString(2));
            m.setAuthor(a1);
            m.setCreatedAt(new Date(cursor.getString(4)));
            historyList.add(m);
        }
        return historyList;

    }


    /*public ArrayList<Message> getPoses(){
        SQLiteDatabase db=getWritableDatabase();
        String[] columns={RetrievalDatabaseHandler.ID, RetrievalDatabaseHandler.NAME, RetrievalDatabaseHandler.DESCRIPTION};
        Cursor cursor=db.query(RetrievalDatabaseHandler.POSES_TABLE, columns, null, null, null, null, null);
        ArrayList<Message> questionsArrayList=new ArrayList<>();

        while(cursor.moveToNext()){
            Message questions=new Message();
            questions.setId(cursor.getString(cursor.getColumnIndex(RetrievalDatabaseHandler.ID)));
            questions.setText(cursor.getString(cursor.getColumnIndex(RetrievalDatabaseHandler.NAME)));
            questionsArrayList.add(questions);
        }
        return questionsArrayList;
    }*/



}