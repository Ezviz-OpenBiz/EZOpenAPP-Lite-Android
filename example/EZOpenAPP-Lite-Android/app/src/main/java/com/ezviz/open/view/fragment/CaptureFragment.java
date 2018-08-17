package com.ezviz.open.view.fragment;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;


import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.scan.camera.CameraManager;
import com.ezviz.open.scan.main.CaptureActivityHandler;
import com.ezviz.open.scan.main.InactivityTimer;
import com.ezviz.open.scan.main.ViewfinderView;
import com.ezviz.open.utils.DataManager;
import com.ezviz.open.utils.EZLog;
import com.ezviz.open.view.avctivity.SearchDeviceActivity;
import com.google.zxing.BarcodeFormat;
import com.ezviz.open.R;
import java.io.IOException;
import java.util.Vector;

/**
 * 自定义实现的扫描Fragment
 */
public class CaptureFragment extends BaseFragment implements SurfaceHolder.Callback {

    private final static String TAG = "CaptureFragment";
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private InactivityTimer mInactivityTimer;

    private CameraManager cameraManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraManager = new CameraManager((mActivity.getApplicationContext()));
        hasSurface = false;
        mInactivityTimer = new InactivityTimer(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture, null);
        viewfinderView = (ViewfinderView) view.findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();
        viewfinderView.setCameraManager(cameraManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera();
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInactivityTimer.shutdown();
        super.onDestroy();
    }


    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }


    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param resultString
     * @param barcode
     *            A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(String resultString, Bitmap barcode) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (resultString == null) {
            EZLog.errorLog(TAG, "handleDecode-> resultString is null");
            return;
        }
        EZLog.errorLog(TAG, "resultString = " + resultString);
        DataManager.DecodeDeviceInfo decodeDeviceInfo = DataManager.getInstance().getDecodeDeviceInfo(resultString);
        if (decodeDeviceInfo != null){
            if (TextUtils.isEmpty(decodeDeviceInfo.deviceSerial)){
                return;
            }
            EZLog.debugLog(TAG, "deviceSerial = " + decodeDeviceInfo.deviceSerial + ",deviceVeryCode = " + decodeDeviceInfo.deviceVerifyCode
                    + ",deviceType = " + decodeDeviceInfo.deviceType);
            Intent intent = new Intent(mContext, SearchDeviceActivity.class);
            intent.putExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL,decodeDeviceInfo.deviceSerial);
            intent.putExtra(EZOpenConstant.EXTRA_DEVICE_VERIFYCODE,decodeDeviceInfo.deviceVerifyCode);
            intent.putExtra(EZOpenConstant.EXTRA_DEVICE_TYPE,decodeDeviceInfo.deviceType);
            mContext.startActivity(intent);
        }
    }



    private void initCamera() {
        try {
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}
