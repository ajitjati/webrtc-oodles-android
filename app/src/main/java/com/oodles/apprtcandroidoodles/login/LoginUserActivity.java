package com.oodles.apprtcandroidoodles.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.oodles.apprtcandroidoodles.AppRTCClient;
import com.oodles.apprtcandroidoodles.CallActivity;
import com.oodles.apprtcandroidoodles.MyPrefs;
import com.oodles.apprtcandroidoodles.PeerConnectionClient;
import com.oodles.apprtcandroidoodles.QuickstartPreferences;
import com.oodles.apprtcandroidoodles.R;
import com.oodles.apprtcandroidoodles.RTCConnection;
import com.oodles.apprtcandroidoodles.WebSocketRTCClient;
import com.oodles.apprtcandroidoodles.curdoperation.CurOperationWebRtc;
import com.oodles.apprtcandroidoodles.util.Keys;
import com.oodles.apprtcandroidoodles.util.Logger;
import com.oodles.apprtcandroidoodles.util.LooperExecutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ankita on 13/4/17.
 */

public class LoginUserActivity extends RTCConnection implements AppRTCClient.SignalingEvents {
    private static final String TAG = "LoginUserActivity";
    private static final int REQUEST_CODE_CONTACTS = 8;
    private static final String FRAGMENT_DIALOG = "dialog";
    RecyclerView loginUserRecycler;
    private static boolean commandLineRun = false;
    private Intent intent = null;
    OnViewClickListener onViewClickListener;
    public String from;
    SearchView searchView;
    MyPrefs myPrefs;
    private boolean callActive;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    int videoWidth = 0;
    int videoHeight = 0;
    int cameraFps = 0;
    int videoStartBitrate = 0;
    int audioStartBitrate = 0;
    String videoCodec;
    String audioCodec;
    boolean aecDump, useOpenSLES;
    private BroadcastReceiver gcmRegistrationBroadcastReceiver;
    private BroadcastReceiver bringToFrontBroadcastReceiver;
    private boolean isGCMReceiverRegistered;
    private boolean isBringToFrontReceiverRegistered;
    boolean captureQualitySlider, displayHud;
    Uri wsurl;
    boolean captureToTexture, hwCodec, noAudioProcessing;
    String roomUrl;
    CurOperationWebRtc curOperationWebRtc;
    private int pos;
    private LoginUserActivity mContext;
    //    boolean renderVideoAndroid;
    private static final String[] CONTACTS_ACCESS_PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
    };
    String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
            " COLLATE LOCALIZED ASC";
    private long mIdColIdx;
    private String mNameColIdx;
    private String mHasPhoneNumberIdx;
    private static final String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    };
    private View progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user_activity);
        mContext = LoginUserActivity.this;
        progressBar = findViewById(R.id.progress_bar);
        myPrefs = new MyPrefs(mContext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Do something for lollipop and above versions
            if (!hasPermissionsGranted(CONTACTS_ACCESS_PERMISSIONS, mContext)) {
                requestWriteReadContactsPermissions(CONTACTS_ACCESS_PERMISSIONS, mContext, REQUEST_CODE_CONTACTS);
                return;
            } else {
                if (!myPrefs.isContactsFetched()) {
                    init();
                } else {
                    curOperationWebRtc = new CurOperationWebRtc(mContext);
                    initViews();
                }
            }
        } else {
            if (!myPrefs.isContactsFetched()) {
                init();
            } else {
                curOperationWebRtc = new CurOperationWebRtc(mContext);
                initViews();
            }
        }


    }

    private void init() {
        curOperationWebRtc = new CurOperationWebRtc(mContext);
        getLoaderManager().initLoader(0, null, contactLoaderManager);

    }


    private boolean hasPermissionsGranted(String[] permissions, Activity mContext) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(mContext, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestWriteReadContactsPermissions(String[] contactsAccessPermissions, Activity mContext, int requestCode) {
        mContext.requestPermissions(contactsAccessPermissions, requestCode);
    }


    private void initViews() {
        initRecycler(curOperationWebRtc.getContacts());
        initAppRtcValues();
    }


    private void initAppRtcValues() {
        roomUrl = /*"https://180.151.230.10:8080/jWebrtc/";*/myPrefs.getRoomUrl();
        wsurl = Uri.parse(roomUrl);
        from = myPrefs.getCallFrom();
        Log.d(TAG, "connecting to:" + wsurl.toString() + "CallFrom" + from);
        Intent intent = getIntent();
//        renderVideoAndroid = intent.getBooleanExtra("renderVideo", true);
//        Log.e("renderVideo", renderVideoAndroid + "");
        getVideoWidthHeight();
        getCameraFps();
        videoBitRate();
        audioBitRate();
        audioVideoCodec();
        hwCapture();
        aceDumpAndUseOpenSsl();
        googlePlayServices();
        captureQualitySlider = myPrefs.isCaptureQualitySlider();
        displayHud = myPrefs.isDisplayHud();
        validUrl();
        Log.e("CallFrom", myPrefs.getCallFrom());
        peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters(
                myPrefs.isVideoCall(),
                myPrefs.isTracing(), videoWidth, videoHeight, cameraFps, videoStartBitrate, videoCodec, hwCodec,
                captureToTexture, audioStartBitrate, audioCodec, noAudioProcessing, aecDump, useOpenSLES);
        roomConnectionParameters = new AppRTCClient.RoomConnectionParameters(wsurl.toString(), from, false);
        Log.i(TAG, "creating appRtcClient with roomUri:" + wsurl.toString() + " from:" + from);
        appRtcClient = new WebSocketRTCClient(this, new LooperExecutor());
        connectToWebsocket();
    }

    private void googlePlayServices() {
        gcmRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = myPrefs.isSentTokenToServer();
                if (sentToken) {
                    logAndToast(getString(R.string.gcm_send_message));
                } else {
                    logAndToast(getString(R.string.gcm_send_message));
                }
            }
        };
        bringToFrontBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent intentStart = new Intent(getApplicationContext(), LoginUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intentStart);
            }
        };
        registerGCMReceiver();
        registerBringToFrontReceiver();
        if (checkPlayServices()) {
            Log.e("FCM_TOKEM", myPrefs.getFcmToken());
        }
    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/");
    }

    private void sendRegistrationToServer(String refreshedToken) {
        //send Token to server here

    }

    private void registerGCMReceiver() {
        if (!isGCMReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(gcmRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isGCMReceiverRegistered = true;
        }
    }

    private void registerBringToFrontReceiver() {
        if (!isBringToFrontReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(bringToFrontBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.INCOMING_CALL));
            isBringToFrontReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "GooglePlayServices are not available.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void validUrl() {
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            intent = new Intent(this, LoginUserActivity.class);
            intent.setData(uri);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, myPrefs.isVideoCall());
            Logger.LogError("value = "," " + myPrefs.isVideoCall());
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            Logger.LogError("value = "," " + videoWidth);
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            Logger.LogError("value = "," " + videoHeight);
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
            Logger.LogError("value = "," " + cameraFps);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            Logger.LogError("value = "," " + captureQualitySlider);
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            Logger.LogError("value = "," " + videoStartBitrate);
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
            Logger.LogError("value = "," " + videoCodec);
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            Logger.LogError("value = "," " + hwCodec);
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            Logger.LogError("value = "," " + captureToTexture);
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            Logger.LogError("value = "," " + noAudioProcessing);
            intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
            Logger.LogError("value = "," " + aecDump);
            intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            Logger.LogError("value = "," " + useOpenSLES);
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            Logger.LogError("value = "," " + audioStartBitrate);
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
            Logger.LogError("value = "," " + audioCodec);
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
            Logger.LogError("value = "," " + displayHud);
            intent.putExtra(CallActivity.EXTRA_TRACING, myPrefs.isTracing());
            Logger.LogError("value = "," " + myPrefs.isTracing());
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
            Logger.LogError("value = "," " + commandLineRun);
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);
            Logger.LogError("value = "," " + runTimeMs);
        }
    }

    LoaderManager.LoaderCallbacks<Cursor> contactLoaderManager = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, // URI
                    PROJECTION,
                    null,
                    null,
                    sortOrder
            );
            progressBar.setVisibility(View.VISIBLE);
            Log.e("ProgressBar", " VISIBLE");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor mCursor) {
           AsyncTask.execute(new Runnable() {
               @Override
               public void run() {
                   mCursor.moveToPosition(0);
                   Log.e("cureosr", mCursor.getCount() + "");
                   Log.e("ProgressBar", " GONE");
                   while (mCursor.moveToNext()) {
                       mIdColIdx = mCursor.getLong(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                       mNameColIdx = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                       mHasPhoneNumberIdx = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                       String phoneNumber = mHasPhoneNumberIdx.trim();
                       Log.e("phoneNumber", mHasPhoneNumberIdx + "");
                       contactHandling(mIdColIdx, mNameColIdx, phoneNumber);
                   }
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           initViews();
                           myPrefs.setContactsFetched(true);
                           progressBar.setVisibility(View.GONE);
                       }
                   });
               }
           });

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
                    Log.e("valueeeeee", myPrefs.getCallFrom());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.clearFocus();
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0);
        } catch (Exception e) {
            Log.e("ChatFragment", "Cause: " + e.getCause());
        }
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(listener);
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (!newText.trim().isEmpty()) {
                contactRecyclerViewAdapter.performFilter(newText);
            } else {
                contactRecyclerViewAdapter.refreshContacts();
            }

            return false;
        }
    };

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gcmRegistrationBroadcastReceiver);
        isGCMReceiverRegistered = false;
        String roomListJson = new JSONArray(roomList).toString();
        myPrefs.setKeyprefRoomList(roomListJson);
        super.onPause();
    }

    @Override
    public void onResume() {
        registerGCMReceiver();
        roomList = new ArrayList<String>();
        String roomListJson = myPrefs.getKeyprefRoomList();
        Log.e("roomListJson", roomListJson);
        if (roomListJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(roomListJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    roomList.add(jsonArray.get(i).toString());
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to load room list: " + e.toString());
            }
        }
        notifyRecyclerAdapter(roomList);
        super.onResume();
    }

    private void notifyRecyclerAdapter(ArrayList<String> roomList) {
        Log.e("OnlineUsersList", roomList + "");
    }

    private void aceDumpAndUseOpenSsl() {
        aecDump = myPrefs.isAecDump();
        useOpenSLES = myPrefs.isUseOpenSLES();
    }

    private void hwCapture() {
        hwCodec = myPrefs.isHwCodec();
        captureToTexture = myPrefs.isCaptureToTexture();
    }

    private void audioVideoCodec() {
        videoCodec = myPrefs.getVideoCodec();
        audioCodec = myPrefs.getAudioCodec();
    }

    private void audioBitRate() {
        String bitrateTypeDefault = "Default";
        String bitrateType = myPrefs.getAudioBitrateType();
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = myPrefs.getAudioBitrateValue();
            audioStartBitrate = Integer.parseInt(bitrateValue);
        }
    }

    private void videoBitRate() {
        String bitrateTypeDefault = "Default";
        String bitrateType = myPrefs.getVideoBitRateType();
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = myPrefs.getVideoBitRateValue();
            videoStartBitrate = Integer.parseInt(bitrateValue);
        }
    }

    private void getCameraFps() {
        String fps = myPrefs.getFps();
        String[] fpsValues = fps.split("[ x]+");
        if (fpsValues.length == 2) {
            try {
                cameraFps = Integer.parseInt(fpsValues[0]);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Wrong camera fps setting: " + fps);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d(TAG, "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            finish();
        }
        if (requestCode == RESULT_OK) {
            if (callActive) {
                connectToUser();
                callActive = false;
            }
        }
    }

    private void getVideoWidthHeight() {
        String resolution = myPrefs.getResolution();
        String[] dimensions = resolution.split("[ x]+");
        if (dimensions.length == 2) {
            try {
                videoWidth = Integer.parseInt(dimensions[0]);
                videoHeight = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                videoWidth = 0;
                videoHeight = 0;
                Log.e(TAG, "Wrong video resolution setting: " + resolution);
            }
        }
    }

    private void clickListen(final ArrayList<Contact> contacts) {
        onViewClickListener = new OnViewClickListener() {
            @Override
            public void setOnViewClickListner(View view, int position) {
               /* pos = position;
                makeCallToUser(contacts, position);*/
                openUserDetailActivity(contacts,position);
            }
        };
    }

    private void openUserDetailActivity(ArrayList<Contact> contacts, int position) {
        Intent userDetailIntent = new Intent(mContext,MainOption.class);
        userDetailIntent.putExtra(Keys.CONNECT_TO,contacts.get(position).getNumber());
        startActivity(userDetailIntent);
    }

    private void makeCallToUser(ArrayList<Contact> contacts, int position) {
        Log.e("position", position + "");
        commandLineRun = false;
        String currentTo = contacts.get(position).getNumber();
        Log.e("currentTo", currentTo);
        String to = currentTo;
        roomConnectionParameters.initiator = true;
        roomConnectionParameters.to = to;
        connectToUser();
    }

    private void connectToUser() {
        Intent newIntent = new Intent(this, CallActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.putExtra("keep", true);
//        newIntent.putExtra("renderVideo",renderVideoAndroid);
        if (intent != null)
            newIntent.putExtras(intent);
        startActivityForResult(newIntent, CONNECTION_REQUEST);
    }

    private void initRecycler(ArrayList<Contact> contacts) {
        loginUserRecycler = (RecyclerView) findViewById(R.id.loginUserRecycler);
        loginUserRecycler.setLayoutManager(new LinearLayoutManager(this));
        loginUserRecycler.setItemAnimator(new DefaultItemAnimator());
        setContactAdapter(contacts);
    }

    private void setContactAdapter(ArrayList<Contact> contacts) {
        contactSorting(contacts);
        clickListen(contacts);
        contactRecyclerViewAdapter = new ContactRecyclerViewAdapter(LoginUserActivity.this, onViewClickListener, contacts);
        loginUserRecycler.setAdapter(contactRecyclerViewAdapter);
    }

    private void contactSorting(ArrayList<Contact> contacts) {
        Collections.sort(contacts, new CustomComparator());

    }

    public class CustomComparator implements Comparator<Contact> {
        @Override
        public int compare(Contact o1, Contact o2) {
            return o2.getOnline().compareTo(o1.getOnline());
        }
    }

    @Override
    public void onWebSocketMessage(String message) {
        //do nothing
    }

    @Override
    public void onWebSocketClose() {
        //do nothing
    }

    @Override
    public void onConnectedToRoom(AppRTCClient.SignalingParameters params) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //onConnectedToRoomInternal(params);
            }
        });
    }

    @Override
    public void onUserListUpdate(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    roomList = new ArrayList();
                    ArrayList<Contact> onlineUsers = curOperationWebRtc.getOnlineContacts("true");
                    for (int i = 0; i < onlineUsers.size(); i++) {
                        curOperationWebRtc.updateOnlineStatus(onlineUsers.get(i).getNumber(), "false");
                        Log.e("AndroidonlineUsers", "" + onlineUsers.get(i).getNumber());
                    }
                    Log.e("AndroidonlineUsers", onlineUsers.size() + "");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        roomList.add(jsonArray.get(i).toString());
                        curOperationWebRtc.updateOnlineStatus(jsonArray.get(i).toString(), "true");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to load room list: " + e.toString());
                }
                Log.e("UserOnlineList", roomList + "");
                setContactAdapter(curOperationWebRtc.getContacts());
            }
        });
    }

    @Override
    public void onIncomingCall(String from, boolean screenSharing) {
        if (!from.equalsIgnoreCase(myPrefs.getCallFrom())) {
            roomConnectionParameters.to = from;
            roomConnectionParameters.initiator = false;
            if (screenSharing) {
                this.callActive = true;
                makeCallToUser(curOperationWebRtc.getContacts(), pos);
            } else {
                RTCConnection.CallDialogFragment newFragment = new RTCConnection.CallDialogFragment();
                Bundle args = new Bundle();
                args.putString("callFrom", from);
                newFragment.setArguments(args);
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if (alert == null) {
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    if (alert == null) {
                        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
                    }
                }
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
                ringtone.play();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(newFragment, "loading");
                transaction.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onIncomingScreenCall(JSONObject from) {
        // super.onIncomingScreenCall()
        logAndToast("Creating OFFER for Screensharing Caller");
        //do nothing here - just in CallActivity

        peerConnectionClient2 = PeerConnectionClient.getInstance(true);

        peerConnectionClient2.createPeerConnectionFactoryScreen(this);

        peerConnectionClient2.createPeerConnectionScreen(peerConnectionClient.getRenderEGLContext(), peerConnectionClient.getScreenRender());
        // Create offer. Offer SDP will be sent to answering client in
        // PeerConnectionEvents.onLocalDescription event.
        peerConnectionClient2.createOffer();

    }

    @Override
    public void onStartCommunication(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
                peerConnectionClient.setRemoteDescription(sdp);
            }
        });
    }

    @Override
    public void onStartScreenCommunication(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient2 == null) {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
                peerConnectionClient2.setRemoteDescription(sdp);
            }
        });
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
                peerConnectionClient.setRemoteDescription(sdp);
            }
        });
    }

    @Override
    public void onRemoteScreenDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient2 == null) {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
                peerConnectionClient2.setRemoteDescription(sdp);
            }
        });
    }

    @Override
    public void onChannelError(String description) {
        Log.e("onChannelError", description);
        logAndToast(description);
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate for non-initilized peer connection.");
                    return;
                }
                peerConnectionClient.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onRemoteScreenIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient2 == null) {
                    Log.e(TAG,
                            "Received ICE candidate for non-initilized peer connection.");
                    return;
                }
                peerConnectionClient2.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onCallback() {
        appRtcClient.sendCallback();
    }

    @Override
    public void onChannelClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("stopCommunication from remotereceived; finishing CallActivity");
                disconnect(false);
            }
        });
    }

    @Override
    public void onChannelScreenClose() {
        Intent intent = new Intent("finish_screensharing");
        sendBroadcast(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Logger.LogDebug(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_CODE_CONTACTS) {
            if (grantResults.length == CONTACTS_ACCESS_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        String errorMsg = getResources().getString(R.string.permission_request_contacts_access);
                        showErrorDialog(errorMsg, mContext);
                        break;
                    } else {
                        if (!myPrefs.isContactsFetched()) {
                            init();
                        } else {
                            curOperationWebRtc = new CurOperationWebRtc(mContext);
                            initViews();
                        }
                    }
                }
            } else {
                String errorMsg = getResources().getString(R.string.permission_request_contacts_access);
                showErrorDialog(errorMsg, mContext);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static void showErrorDialog(String errorMsg, Activity mContext) {
        ErrorDialog.newInstance(errorMsg)
                .show(mContext.getFragmentManager(), FRAGMENT_DIALOG);
    }

    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            FragmentCompat.requestPermissions(parent,WRITE_READ_PERMISSIONS ,
//                                    REQUEST_CODE_VIDEO);
                        }
                    })
                    .create();
        }

    }

}
