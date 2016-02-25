package com.imtuandung.demorecordingcall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class RecorderCallService extends Service {
    private final String TAG = "RecorderCallService";
    private MediaRecorder mMediaRecorder;
    private boolean mIsRecording ;
    private final String FOLDER_NAME = "CallRec";
    private String mFileType = ".3gp";
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //
        if (mTelephonyManager == null) {
            mIsRecording = false;
            Log.d(TAG, "start listen phone state");
            mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneStateListener = new PhoneStateListener();
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        //
        return START_STICKY;
    }

    private void startRecording(String phoneNumber) {
        Log.d(TAG, " mIsRecording : " + mIsRecording);

        //
        String outPutFile = getOutputFile(phoneNumber);
        if (outPutFile != null) {

            setupMediaRecorder(MediaRecorder.AudioSource.VOICE_CALL, MediaRecorder.OutputFormat.THREE_GPP, MediaRecorder.AudioEncoder.AMR_NB);
            //setupBitRate();
            setOutPutFile(outPutFile);
            try {

                mMediaRecorder.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "mMediaRecorder is trying start");
            try {
                mMediaRecorder.start();
                mIsRecording = true;
                Log.d(TAG, "mMediaRecorder start success mIsRecording : " + mIsRecording);
            } catch (Exception e) {
                Log.e(TAG, "mMediaRecorder start unsuccessful");
            }
        }

    }

    /**
     *
     */
    private void setupMediaRecorder(int audioSource, int outputFormat, int audioEncoder) {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(audioSource);
        mMediaRecorder.setOutputFormat(outputFormat);
        mMediaRecorder.setAudioEncoder(audioEncoder);
    }

    private void setupBitRate(int encodingBitrate, int samplingBitrate) {
        mMediaRecorder.setAudioEncodingBitRate(encodingBitrate);
        mMediaRecorder.setAudioSamplingRate(samplingBitrate);
    }

    private void setOutPutFile(String path) {
        mMediaRecorder.setOutputFile(path);
    }

    private String getOutputFile(String phoneNumber) {
        long time = System.currentTimeMillis();
        String nameFile = phoneNumber + "_" + time;
        File file = new File(Environment.getExternalStorageDirectory(), "/" + FOLDER_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
        File recorderFile = null;
        try {
            recorderFile = File.createTempFile(nameFile, mFileType, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (recorderFile != null) {
            return recorderFile.getAbsolutePath();
        }
        return null;
    }

    private void stopRecording() {
        //
        if (mIsRecording) {
            if (mMediaRecorder != null) {
                Log.d(TAG, "mMediaRecorder.stop()");
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mIsRecording = false;
            }
        }
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        //stop service
        stopSelf();
    }

    //
    private class PhoneStateListener extends android.telephony.PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (incomingNumber != null && !incomingNumber.equals("")) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(TAG, " phone state : CALL_STATE_IDLE");
                        stopRecording();

                        break;
                    case TelephonyManager.CALL_STATE_RINGING:

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d(TAG, " phone state : CALL_STATE_OFFHOOK mIsRecording " + mIsRecording );
                        if (!mIsRecording) {
                            startRecording(incomingNumber);
                        }
                        break;
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " onDestroy");
        super.onDestroy();
    }
}