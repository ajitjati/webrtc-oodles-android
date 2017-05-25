/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.oodles.apprtcandroidoodles;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.webrtc.RendererCommon.ScalingType;

/**
 * Fragment for call control.
 */
public class CallFragment extends Fragment implements View.OnClickListener {

    private View controlView;
    private TextView contactView;
    private ImageButton disconnectButton;
    public ImageButton audioButton;
    private ImageButton cameraSwitchButton;
    private ImageButton videoScalingButton;
    private OnCallEvents callEvents;
    private ScalingType scalingType;
    private boolean videoCallEnabled = true;


    public interface OnCallEvents {
        void onCallHangUp();

        void onCameraSwitch(ImageButton cameraSwitchButton);

        void onAudioMute(ImageButton audioButton);

        void onVideoScalingSwitch(ScalingType scalingType);

        void onCaptureFormatChange(int width, int height, int framerate);
    }

    /**
     * Call control interface for container activity.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        controlView = inflater.inflate(R.layout.fragment_call, container, false);
        initCallFragment();
        return controlView;
    }

    private void initCallFragment() {
        contactView = (TextView) controlView.findViewById(R.id.contact_name_call);
        disconnectButton = (ImageButton) controlView.findViewById(R.id.button_call_disconnect);
        audioButton = (ImageButton) controlView.findViewById(R.id.button_audio_on_off);
        cameraSwitchButton = (ImageButton) controlView.findViewById(R.id.button_call_switch_camera);
        videoScalingButton = (ImageButton) controlView.findViewById(R.id.button_call_scaling_mode);
        disconnectButton.setOnClickListener(this);
        audioButton.setOnClickListener(this);
        cameraSwitchButton.setOnClickListener(this);
        videoScalingButton.setOnClickListener(this);
        scalingType = ScalingType.SCALE_ASPECT_FILL;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_call_disconnect:
                callEvents.onCallHangUp();
                break;
            case R.id.button_audio_on_off:
                callEvents.onAudioMute(audioButton);
                break;
            case R.id.button_call_switch_camera:
                callEvents.onCameraSwitch(cameraSwitchButton);
                break;
            case R.id.button_call_scaling_mode:
                scalingModeCoding();
                break;

        }
    }

    private void scalingModeCoding() {
        if (scalingType == ScalingType.SCALE_ASPECT_FILL) {
            videoScalingButton.setBackgroundResource(
                    R.drawable.ic_action_full_screen);
            scalingType = ScalingType.SCALE_ASPECT_FIT;
        } else {
            videoScalingButton.setBackgroundResource(
                    R.drawable.ic_action_return_from_full_screen);
            scalingType = ScalingType.SCALE_ASPECT_FILL;
        }
        callEvents.onVideoScalingSwitch(scalingType);
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean captureSliderEnabled = true;
        Bundle args = getArguments();
        if (args != null) {
            contactView.setText(RTCConnection.roomConnectionParameters.to + " Calling...");
            videoCallEnabled = args.getBoolean(CallActivity.EXTRA_VIDEO_CALL, true);
//            captureSliderEnabled = videoCallEnabled
//                    && args.getBoolean(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
        }
        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }
//        if (captureSliderEnabled) {
//            captureFormatSlider.setOnSeekBarChangeListener(new CaptureQualityController(captureFormatText, callEvents));
//        } else {
//            captureFormatText.setVisibility(View.GONE);
//            captureFormatSlider.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }

}
