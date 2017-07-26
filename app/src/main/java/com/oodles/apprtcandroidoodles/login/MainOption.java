package com.oodles.apprtcandroidoodles.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.ArrayList;

import smackconnection.SmackChatting;

import static com.oodles.apprtcandroidoodles.RTCConnection.CONNECTION_REQUEST;
import static com.oodles.apprtcandroidoodles.RTCConnection.appRtcClient;
import static com.oodles.apprtcandroidoodles.RTCConnection.isWSSUrl;
import static com.oodles.apprtcandroidoodles.RTCConnection.isWSUrl;
import static com.oodles.apprtcandroidoodles.RTCConnection.roomConnectionParameters;

/**
 * Created by ankita on 28/4/17.
 */

public class MainOption extends RTCConnection  implements View.OnClickListener {

    private static final String[] CONTACTS_ACCESS_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };
    private static final int REQUEST_CODE_VIDEO = 11;
    private static final String TAG = MainOption.class.getSimpleName();
    private static final String FRAGMENT_DIALOG = "dialog";
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
    //CurOperationWebRtc curOperationWebRtc;
    MyPrefs myPrefs;
    private Activity mContext;
    private String connectTo;
    private static boolean commandLineRun = false;
    private Uri wsurl;
    private String roomUrl;
    private String from;
    int videoWidth = 0;
    int videoHeight = 0;
    int cameraFps = 0;
    int videoStartBitrate = 0;
    int audioStartBitrate = 0;
    String videoCodec;
    String audioCodec;
    boolean captureToTexture, hwCodec, noAudioProcessing;
    boolean aecDump, useOpenSLES;
    boolean captureQualitySlider, displayHud;
    private Intent intent = null;
    public long callStartedTimeMs = 0;
    private BroadcastReceiver gcmRegistrationBroadcastReceiver;
    private boolean doToast;
    private Toast logToast;
    private BroadcastReceiver bringToFrontBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean isGCMReceiverRegistered;
    private boolean isBringToFrontReceiverRegistered;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_option);
        mContext = MainOption.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Do something for lollipop and above versions
            if (!hasPermissionsGranted(CONTACTS_ACCESS_PERMISSIONS, mContext)) {
                requestWriteReadContactsPermissions(CONTACTS_ACCESS_PERMISSIONS, mContext, REQUEST_CODE_VIDEO);
                return;
            } else {
               initActivity();
            }
        } else {
            initActivity();
        }
        getBundledData();


    }

    private void getBundledData() {
        Bundle bundle = getIntent().getExtras();
        connectTo = bundle.getString(Keys.CONNECT_TO);
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
//        appRtcClient = new WebSocketRTCClient((AppRTCClient.SignalingEvents) this, new LooperExecutor());
        connectToWebsocket();
    }

    public void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (doToast) {
            if (logToast != null) {
                logToast.cancel();
            }
            logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            logToast.show();
        }

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

    public void connectToWebsocket() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }
        callStartedTimeMs = System.currentTimeMillis();
        // Start room connection.
        appRtcClient.connectToWebsocket(roomConnectionParameters);
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

    public boolean validateUrl(String url) {
        //if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
        if (isWSUrl(url) || isWSSUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
        return false;
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


    private void initActivity() {
        //curOperationWebRtc = new CurOperationWebRtc(MainOption.this);
        myPrefs = new MyPrefs(MainOption.this);
//        getLoaderManager().initLoader(0, null, contactLoaderManager);
        videoCalling = (CardView) findViewById(R.id.videoCalling);
        audioCalling = (CardView) findViewById(R.id.audioCalling);
        chatting = (CardView) findViewById(R.id.chattingAndroid);
        videoCalling.setOnClickListener(this);
        audioCalling.setOnClickListener(this);
        chatting.setOnClickListener(this);
        initAppRtcValues();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestWriteReadContactsPermissions(String[] contactsAccessPermissions, Activity mContext, int requestCode) {
        mContext.requestPermissions(contactsAccessPermissions, requestCode);
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

    private void makeCallToUser(/*ArrayList<Contact> contacts, int position*/) {
//        Log.e("position", position + "");
        commandLineRun = false;
//        String currentTo = contacts.get(position).getNumber();
//        Log.e("currentTo", currentTo);
        String to = connectTo;
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


   /* LoaderManager.LoaderCallbacks<Cursor> contactLoaderManager = new LoaderManager.LoaderCallbacks<Cursor>() {
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
    };*/

    /*private void contactHandling(long mIdColIdx, String mNameColIdx, String phoneNumber) {
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
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.videoCalling:
/*                Intent intent = new Intent(MainOption.this, LoginUserActivity.class);
//                intent.putExtra("renderVideo",true);
                startActivity(intent);*/
                makeCallToUser();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Logger.LogDebug(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_CODE_VIDEO) {
            if (grantResults.length == CONTACTS_ACCESS_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        String errorMsg = getResources().getString(R.string.permission_request_contacts_access);
                        showErrorDialog(errorMsg, mContext);
                        break;
                    } else {
                        initActivity();
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

    @Override
    public void onWebSocketMessage(String message) {

    }

    @Override
    public void onWebSocketClose() {

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
