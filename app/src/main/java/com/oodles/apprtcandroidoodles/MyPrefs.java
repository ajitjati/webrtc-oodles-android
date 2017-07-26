package com.oodles.apprtcandroidoodles;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by oodles on 6/1/16.
 */
public class MyPrefs {

    long aLong = System.currentTimeMillis();
    String currentMillis = String.valueOf(aLong);
    private final String DBNAME = "AppRtcPrefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;
    private Context context;
    private final String CALL_FROM = "callFrom";
    private final String LOGIN = "login";
    private final String VIDEO_CALL = "videoCall";
    private final String TRACING = "tracing";
    private final String RESOLUTION = "resolution";
    private final String FPS = "fps";
    private final String VIDEO_BITRATE_TYPE = "videoBitrateType";
    private final String VIDEO_BITRATE_VALUE = "videoBitrateValue";
    private final String AUDIO_BITRATE_TYPE = "audioBitrateType";
    private final String AUDIO_BITRATE_VALUE = "audioBitrateValue";
    private final String VIDEO_CODEC = "videoCodec";
    private final String AUDIO_CODEC = "audioCodec";
    private final String HW_CODEC = "hwCodec";
    private final String CONTACTS_FETCHED = "contactsFetched";
    private final String CAPTURE_TO_TEXTURE = "captureToTexture";
    private final String AEC_DUMP = "aecDump";
    private final String USE_OPEN_SLES = "useOpenSLES";
    private final String ROOM_URL = "roomUrl";
    private final String QUALITY_SLIDER = "captureQualitySlider";
    private final String DISPLAY_HUD = "displayHud";
    private final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    private final String ROOM_LIST = "keyprefRoomList";
    private final String FCMTOKEN = "fcmToken";



    String fcmToken;
    boolean displayHud;


    public MyPrefs(Context context) {
        this.context = context;
        try {
            sharedPreferences = this.context.getSharedPreferences(DBNAME,
                    Context.MODE_PRIVATE);
            spEditor = sharedPreferences.edit();
        } catch (Exception e) {
            Log.e("Exception Prefs", e.getCause() + "");
        }
    }


    public String getRoomUrl() {
        return sharedPreferences.getString(ROOM_URL, "https://180.151.230.12:9443/jWebrtc/");
    }

    public void setRoomUrl(String roomUrl) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(ROOM_URL, roomUrl);
        spEditor.commit();
    }

    public boolean isCaptureQualitySlider() {
        return sharedPreferences.getBoolean(QUALITY_SLIDER, false);
    }

    public void setCaptureQualitySlider(boolean captureQualitySlider) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(QUALITY_SLIDER, captureQualitySlider);
        spEditor.commit();
    }

    public boolean isDisplayHud() {
        return sharedPreferences.getBoolean(DISPLAY_HUD, false);
    }

    public void setDisplayHud(boolean displayHud) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(DISPLAY_HUD, displayHud);
        spEditor.commit();
    }

    public boolean isSentTokenToServer() {
        return sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
    }

    public void setSentTokenToServer(boolean sentTokenToServer) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(SENT_TOKEN_TO_SERVER, displayHud);
        spEditor.commit();
    }
    public String getKeyprefRoomList() {
        return sharedPreferences.getString(ROOM_LIST, "");
    }

    public void setKeyprefRoomList(String keyprefRoomList) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(ROOM_LIST, keyprefRoomList);
        spEditor.commit();
    }


    public boolean isLogin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void setLogin(boolean login) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(LOGIN, login);
        spEditor.commit();
    }

    public boolean isHwCodec() {
        return sharedPreferences.getBoolean(HW_CODEC, true);

    }

    public void setHwCodec(boolean hwCodec) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(HW_CODEC, hwCodec);
        spEditor.commit();
    }

    public void setContactsFetched(boolean isFetched) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(CONTACTS_FETCHED, isFetched);
        spEditor.commit();
    }

    public boolean isContactsFetched() {
        return sharedPreferences.getBoolean(CONTACTS_FETCHED, false);

    }

    public boolean isCaptureToTexture() {
        return sharedPreferences.getBoolean(CAPTURE_TO_TEXTURE, false);
    }

    public void setCaptureToTexture(boolean captureToTexture) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(CAPTURE_TO_TEXTURE, captureToTexture);
        spEditor.commit();
    }

    public boolean isAecDump() {
        return sharedPreferences.getBoolean(AEC_DUMP, false);
    }

    public void setAecDump(boolean aecDump) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(AEC_DUMP, aecDump);
        spEditor.commit();
    }

    public boolean isUseOpenSLES() {
        return sharedPreferences.getBoolean(USE_OPEN_SLES, false);
    }

    public void setUseOpenSLES(boolean useOpenSLES) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(USE_OPEN_SLES, useOpenSLES);
        spEditor.commit();
    }

    public String getCallFrom() {
        return sharedPreferences.getString(CALL_FROM, "8874675724");
    }


    public void setCallFrom(String callFrom) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(CALL_FROM, callFrom);
        spEditor.commit();
    }

    public String getVideoBitRateType() {
        return sharedPreferences.getString(VIDEO_BITRATE_TYPE, "Default");
    }

    public void setVideoBitRateType(String videoBitRateType) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(VIDEO_BITRATE_TYPE, videoBitRateType);
        spEditor.commit();
    }

    public String getVideoBitRateValue() {
        return sharedPreferences.getString(VIDEO_BITRATE_VALUE, "1000");
    }

    public void setVideoBitRateValue(String videoBitRateValue) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(VIDEO_BITRATE_VALUE, videoBitRateValue);
        spEditor.commit();
    }

    public String getAudioBitrateType() {
        return sharedPreferences.getString(AUDIO_BITRATE_TYPE, "Default");
    }

    public void setAudioBitrateType(String audioBitrateType) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(AUDIO_BITRATE_TYPE, audioBitrateType);
        spEditor.commit();
    }

    public String getAudioBitrateValue() {
        return sharedPreferences.getString(AUDIO_BITRATE_VALUE, "Default");
    }

    public void setAudioBitrateValue(String audioBitrateValue) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(AUDIO_BITRATE_VALUE, audioBitrateValue);
        spEditor.commit();
    }

    public boolean isTracing() {
        return sharedPreferences.getBoolean(TRACING, false);
    }

    public void setTracing(boolean tracing) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(TRACING, tracing);
        spEditor.commit();
    }

    public String getResolution() {
        return sharedPreferences.getString(RESOLUTION, "Default");
    }

    public void setResolution(String resolution) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(RESOLUTION, resolution);
        spEditor.commit();
    }

    public boolean isVideoCall() {
        return sharedPreferences.getBoolean(VIDEO_CALL, true);
    }

    public void setVideoCall(boolean videoCall) {
        spEditor = sharedPreferences.edit();
        spEditor.putBoolean(VIDEO_CALL, videoCall);
        spEditor.commit();
    }

    public String getFps() {
        return sharedPreferences.getString(FPS, "Default");
    }

    public void setFps(String fps) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(FPS, fps);
        spEditor.commit();
    }

    public String getVideoCodec() {
        return sharedPreferences.getString(VIDEO_CODEC, "VP8");
    }

    public void setVideoCodec(String videoCodec) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(VIDEO_CODEC, videoCodec);
        spEditor.commit();
    }

    public String getAudioCodec() {
        return sharedPreferences.getString(AUDIO_CODEC, "OPUS");
    }

    public void setAudioCodec(String audioCodec) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(AUDIO_CODEC, audioCodec);
        spEditor.commit();
    }

    public void clear() {
        spEditor.clear();
        spEditor.commit();
    }
    public String getFcmToken() {
        return sharedPreferences.getString(FCMTOKEN, "");
    }

    public void setFcmToken(String fcmToken) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(FCMTOKEN, fcmToken);
        spEditor.commit();
    }
}