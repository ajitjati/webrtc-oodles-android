package com.oodles.apprtcandroidoodles.login;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.oodles.apprtcandroidoodles.MyPrefs;
import com.oodles.apprtcandroidoodles.R;
import com.oodles.apprtcandroidoodles.curdoperation.CurOperationWebRtc;

import smackconnection.SmackChatting;

/**
 * Created by ankita on 28/4/17.
 */

public class MainOption extends AppCompatActivity implements View.OnClickListener {

    CardView videoCalling, audioCalling, chatting;
    private static final String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    };
    String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
            " COLLATE LOCALIZED ASC";
    private long mIdColIdx;
    private String mNameColIdx;
    private String mHasPhoneNumberIdx;
    private String onlineStatus = "false";
    CurOperationWebRtc curOperationWebRtc;
    MyPrefs myPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_option);
        curOperationWebRtc = new CurOperationWebRtc(MainOption.this);
        myPrefs = new MyPrefs(MainOption.this);
        getLoaderManager().initLoader(0, null, contactLoaderManager);
        videoCalling = (CardView) findViewById(R.id.videoCalling);
        audioCalling = (CardView) findViewById(R.id.audioCalling);
        chatting = (CardView) findViewById(R.id.chattingAndroid);
        videoCalling.setOnClickListener(this);
        audioCalling.setOnClickListener(this);
        chatting.setOnClickListener(this);
    }

    LoaderManager.LoaderCallbacks<Cursor> contactLoaderManager = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(MainOption.this,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, // URI
                    PROJECTION,
                    null,
                    null,
                    sortOrder
            );
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
            mCursor.moveToPosition(0);
            Log.e("cureosr", mCursor.getCount() + "");
            while (mCursor.moveToNext()) {
                mIdColIdx = mCursor.getLong(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                mNameColIdx = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                mHasPhoneNumberIdx = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String phoneNumber = mHasPhoneNumberIdx.trim();
                Log.e("phoneNumber", mHasPhoneNumberIdx + "");
                contactHandling(mIdColIdx, mNameColIdx, phoneNumber);

            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private void contactHandling(long mIdColIdx, String mNameColIdx, String phoneNumber) {
        if (phoneNumber.length() >= 10) {
            if (phoneNumber.contains(" ")) {
                String newNumber = phoneNumber.replace(" ", "");
                if (newNumber.length() >= 10) {
                    String newMobileNumber = newNumber.substring(newNumber.length() - 10);
                    Uri profileImage = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, mIdColIdx);
                   Log.e("valueeeeee",myPrefs.getCallFrom());
                    if (!newMobileNumber.equalsIgnoreCase(myPrefs.getCallFrom())) {
                        curOperationWebRtc.insertContacts(mIdColIdx, mNameColIdx, newMobileNumber, profileImage, "false");
                    }
                }
            } else {
                String newMobileNumber = phoneNumber.substring(phoneNumber.length() - 10);
                Uri profileImage = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, mIdColIdx);
                if (!newMobileNumber.equalsIgnoreCase(myPrefs.getCallFrom())) {
                    curOperationWebRtc.insertContacts(mIdColIdx, mNameColIdx, newMobileNumber, profileImage, "false");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.videoCalling:
                Intent intent = new Intent(MainOption.this, LoginUserActivity.class);
//                intent.putExtra("renderVideo",true);
                startActivity(intent);
                break;
            case R.id.audioCalling:
                Intent intentAudio = new Intent(MainOption.this, LoginUserActivity.class);
//                intentAudio.putExtra("renderVideo",false);
                startActivity(intentAudio);
                break;
            case R.id.chattingAndroid:
                Intent smackConnection = new Intent(MainOption.this, SmackChatting.class);
                startActivity(smackConnection);
                break;
        }
    }
}
