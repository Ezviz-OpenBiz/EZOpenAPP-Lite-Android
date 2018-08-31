package com.ezviz.open.view.avctivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.ezviz.open.common.WindowSizeChangeNotifier;
import com.ezviz.open.presenter.PlayBackPresenter;
import com.ezviz.open.utils.DataManager;
import com.ezviz.open.utils.DateUtil;
import com.ezviz.open.utils.EZLog;
import com.ezviz.open.utils.EZOpenUtils;
import com.ezviz.open.view.PlayBackView;
import com.ezviz.open.view.widget.CommomAlertDialog;
import com.ezviz.open.view.widget.EZUIPlayerView;
import com.ezviz.open.view.widget.ExRelativeLayout;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.OnEZPlayerCallBack;
import com.videogo.openapi.bean.EZAlarmInfo;
import com.videogo.openapi.bean.EZCloudRecordFile;
import com.videogo.openapi.bean.EZDeviceRecordFile;


import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import com.ezviz.open.R;
/**
 * Description:消息回放页面
 * Created by dingwei3
 *
 * @date : 2016/12/20
 */
public class PlayBackActivity extends RootActivity implements SurfaceHolder.Callback, PlayBackView, View.OnClickListener, WindowSizeChangeNotifier.OnWindowSizeChangedListener, Animator.AnimatorListener {

    private static final String TAG = "PlayBackActivity";
    public final static int STATUS_INIT = 1;
    public final static int STATUS_START = 2;
    public final static int STATUS_PLAY = 3;
    public final static int STATUS_STOP = 4;
    public final static int STATUS_PAUSE = 5;



    /**
     * 播放器状态
     */
    public int mStatus = STATUS_INIT;

    private static final int MSG_REFRESH_PLAY_UI = 1000;
    private static final int MSG_HIDE_TOPBAR = 1001;
    private static final int MSG_SHOW_TOPBAR = 1002;
    private static final int MSG_REFRESH_PLAY_PROGRESS = 1003;
    private static final int SHOW_TOP_BAR_TIME = 5000;


    private PlayBackPresenter mPlayBackPresenter;
    private ImageView mBack;

    private EZPlayer mEZPlayer;
    private String mDeviceSerial;
    private int mCameraNo = -1;
    private int mOrientation;
    private String mVerifyCode;
    private TextView mTitleTextView;
    private TextView mDescTextView;
    private ImageView mFullScreenBtn;
    private SeekBar mPlaySeekBar;

    private PlayBackActivity.PlayUI mPlayUI;

    private boolean isSoundOpen = true;
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private int mCurrentVolume;
    private SeekBar mVolumeSeekBar;
    private PlayBackActivity.VolumeReceiver mVolumeReceiver;
    private TextView mRateTextView;
    private TextView mFlowTextView;
    private RelativeLayout mTopBar;
    private LinearLayout mRecordLayout;
    private ImageView mRecordingImg;
    private TextView mRecordTimeTextView;
    private TextView mStartTimeTextView;
    private TextView mEndTimeTextView;
    private String mRecordPath;
    /**
     * 静音
     */
    private ImageView mVoiceLeast;

    /**
     * 最大音量
     */
    private ImageView mVoiceLoudest;

    /**
     * 存放上一次的流量
     */
    private long mStreamFlow = 0;

    /**
     * 当前流量
     */
    private int mRealFlow = 0;

    private String mLastOSDTime;
    /**
     * 录像时长，单位秒
     */
    private int mRecordTime = 0;

    private int mUiOptions = 0;
    private boolean mIsRecording;
    private PlayBackActivity.MyOrientationDetector mOrientationDetector;
    /**
     * 横屏模式下顶部topbar
     */
    private ExRelativeLayout mOverlayTopBar;
    private ImageView mOverlayTopBarImgBack;

    private AlphaAnimation mHideAnimation;
    private AlphaAnimation mShowAnimation;
    private AnimatorSet animSetXY;
    private boolean isSurfaceCanClick = true;
    private boolean isPreparePlay = false;

    private EZAlarmInfo mEZAlarmInfo;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private int mAlarmMaxDuration = 0;
    private static final int ALARM_PRE_TIME = 10;

    private static final int ALARM_MAX_DURATION = 40;

    /**
     * resume时是否恢复播放
     */
    private AtomicBoolean isResumePlay = new AtomicBoolean(true);

    /**
     * surface是否创建好
     */
    private AtomicBoolean isInitSurface = new AtomicBoolean(false);

    private EZCloudRecordFile mEZCloudRecordFile;

    private EZDeviceRecordFile mEZDeviceRecordFile;

    private EZUIPlayerView mEZUIPlayerView;

    private Calendar mPlayTime;

    @Override
    public void onAnimationStart(Animator animator) {
        isSurfaceCanClick = false;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        isSurfaceCanClick = true;
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }


    class PlayUI implements View.OnClickListener {
        /**
         * 录像
         */
        ImageView mRecordImg;
        /**
         * 播放/暂停
         */
        ImageView mPlayImg;
        /**
         * 拍照
         */
        ImageView mPictureImg;

        @Override
        public void onClick(View view) {
            if (mPlayImg == view) {
                if (mStatus == STATUS_PLAY || mStatus == STATUS_START) {
                    pauseRealPlay();
                } else if (mStatus == STATUS_STOP) {
                    prepareStartRealPlay();
                } else if (mStatus == STATUS_PAUSE) {
                    resumeRealPlay();
                }
            } else if (mPictureImg == view) {
                mPlayBackPresenter.savePicture(PlayBackActivity.this, mEZPlayer);
            } else if (mRecordImg == view) {
                startRecord();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        // 保持屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_back);
        mPlayBackPresenter = new PlayBackPresenter(this);
        initView();
        initData();
        registerVolumeReceiver();
        setSurfaceSize();
    }

    private void initView() {
        mEZUIPlayerView = (EZUIPlayerView) findViewById(R.id.play_view);
        mEZUIPlayerView.setOnClickListener(this);
        mEZUIPlayerView.getSurfaceView().getHolder().addCallback(this);
        mTopBar = (RelativeLayout) findViewById(R.id.play_topbar);
        mVolumeSeekBar = (SeekBar) findViewById(R.id.volume_seekbar);
        mOverlayTopBar = (ExRelativeLayout) findViewById(R.id.overlay_top_bar);
        mBack = (ImageView) findViewById(R.id.image_back);
        mRateTextView = (TextView) findViewById(R.id.rate_text);
        mFlowTextView = (TextView) findViewById(R.id.flow_text);
        mRecordLayout = (LinearLayout) findViewById(R.id.record_layout);
        mRecordingImg = (ImageView) findViewById(R.id.record_img);
        mRecordTimeTextView = (TextView) findViewById(R.id.record_text);
        mBack.setOnClickListener(this);
        mTitleTextView = (TextView) findViewById(R.id.camera_name);
        mDescTextView = (TextView) findViewById(R.id.camera_type);
        mFullScreenBtn = (ImageView) findViewById(R.id.btn_full);
        mOverlayTopBarImgBack = (ImageView) findViewById(R.id.overlay_image_back);
        mOverlayTopBarImgBack.setOnClickListener(this);
        mFullScreenBtn.setOnClickListener(this);
        mVoiceLeast = (ImageView) findViewById(R.id.btn_voice_least);
        mVoiceLoudest = (ImageView) findViewById(R.id.btn_voice_loudest);
        mStartTimeTextView = (TextView) findViewById(R.id.text_start_time);
        mEndTimeTextView = (TextView) findViewById(R.id.text_end_time);
        mPlaySeekBar = (SeekBar) findViewById(R.id.play_seekbar);
        mPlaySeekBar.setEnabled(false);
        mPlaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mStartTimeTextView.setText(DateUtil.parseTimeToString(i * 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(MSG_REFRESH_PLAY_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress == mAlarmMaxDuration) {
                    return;
                }
                if (mEZPlayer != null) {
                    stopRecord();
                    Calendar seekTime = Calendar.getInstance();
                    seekTime.setTimeInMillis(mStartTime.getTimeInMillis() + progress * 1000);
                    Log.d(TAG, "seekPlayback time = " + DateUtil.getDataTime(seekTime.getTimeInMillis(), DateUtil.simpleDateFormat_yyyyMMddHHmmss));
                    startRealPlayUI();
                    mEZPlayer.seekPlayback(seekTime);
                }
            }
        });
        mVoiceLeast.setOnClickListener(this);
        mVoiceLoudest.setOnClickListener(this);
        animSetXY = new AnimatorSet();
        animSetXY.addListener(this);
        initPlayUI();
    }

    /**
     * 初始化播放控制相关UI
     */
    private void initPlayUI() {
        mPlayUI = new PlayBackActivity.PlayUI();
        mPlayUI.mRecordImg = (ImageView) findViewById(R.id.img_record);
        mPlayUI.mPlayImg = (ImageView) findViewById(R.id.img_play);
        mPlayUI.mPictureImg = (ImageView) findViewById(R.id.img_picture);
        mPlayUI.mPlayImg.setOnClickListener(mPlayUI);
        mPlayUI.mPictureImg.setOnClickListener(mPlayUI);
        mPlayUI.mRecordImg.setOnClickListener(mPlayUI);
        mPlayUI.mRecordImg.setEnabled(false);
        mPlayUI.mPictureImg.setEnabled(false);
    }

    private void initData() {
        Intent intent = getIntent();
        mEZAlarmInfo = intent.getParcelableExtra(EZOpenUtils.EXTRA_ALARM_INFO);
        if (mEZAlarmInfo == null) {
            EZLog.d(TAG, "mEZAlarmInfo is null");
            finish();
            return;
        }
        mDeviceSerial = mEZAlarmInfo.getDeviceSerial();
        mCameraNo = mEZAlarmInfo.getCameraNo();
        if (TextUtils.isEmpty(mDeviceSerial) || mCameraNo == -1) {
            EZLog.d(TAG, "mDeviceSerial or mCameraNo is null");
            finish();
            return;
        }
        mStartTime = EZOpenUtils.parseTimeToCalendar(mEZAlarmInfo.getAlarmStartTime());
        int preTime = mEZAlarmInfo.getPreTime();
        mStartTime.add(Calendar.SECOND, -(preTime > 0 ? preTime : ALARM_PRE_TIME));
        mEndTime = (Calendar) mStartTime.clone();
        int delayTime = mEZAlarmInfo.getDelayTime();
        mAlarmMaxDuration = (delayTime + preTime) > 0 ? (delayTime + preTime) : ALARM_MAX_DURATION;
        mEndTime.add(Calendar.SECOND, mAlarmMaxDuration);
        mPlaySeekBar.setProgress(0);
        mPlaySeekBar.setMax(mAlarmMaxDuration);
        mEZPlayer = EZPlayer.createPlayer(mDeviceSerial, mCameraNo);
        mEZPlayer.setOnEZPlayerCallBack(new OnEZPlayerCallBack() {
            @Override
            public void onPlaySuccess() {
                EZLog.d(TAG, "onPlaySuccess");
                if (mStatus != STATUS_STOP) {
                    handlePlaySuccess();
                }
            }

            @Override
            public void onPlayFailed(BaseException e) {
                EZLog.d(TAG, "onPlayFailed");
                handlePlayFail(e);
            }

            @Override
            public void onVideoSizeChange(int i, int i1) {
                EZLog.d(TAG, "onVideoSizeChange");
                int mVideoWidth = i;
                int mVideoHeight = i1;
                EZLog.d(TAG, "video width = " + mVideoWidth + "   height = " + mVideoHeight);
                if (mStatus != STATUS_STOP) {
                    mEZUIPlayerView.setVideoSizeChange(mVideoWidth, mVideoHeight);
                }
            }

            @Override
            public void onCompletion() {
                EZLog.d(TAG, "onCompletion");
                handlePlayFinish();
            }
        });

        mPlayBackPresenter.preparePlayBackInfo(mDeviceSerial);
        mPlayBackPresenter.blurBitmap(this, ((ImageView) findViewById(R.id.img_main)), mEZAlarmInfo.getAlarmPicUrl(), 25f);
        if (mEZAlarmInfo.getRecState() == 1 || mEZAlarmInfo.getRecState() == 5) {
            mPlayBackPresenter.searchRecordFileFromCloud(mDeviceSerial, mCameraNo, mStartTime, mEndTime);
        } else {
            mPlayBackPresenter.searchRecordFileFromDevice(mDeviceSerial, mCameraNo, mStartTime, mEndTime);
        }

        mOrientationDetector = new PlayBackActivity.MyOrientationDetector(this);
        mUiOptions = getWindow().getDecorView().getSystemUiVisibility();
        new WindowSizeChangeNotifier(this, this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVolumeSeekBar.setMax(mMaxVolume);
        mVolumeSeekBar.setProgress(mCurrentVolume);
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mVolumeSeekBar.setProgress(mCurrentVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    /**
     * 更新UI
     */
    private void refreshUI() {
        mTitleTextView.setText(mEZAlarmInfo.getAlarmName());
        mDescTextView.setText(mEZAlarmInfo.getCategory() + "(" + mEZAlarmInfo.getDeviceSerial() + ")");
    }

    /**
     * 更新播放状态显示UI
     */
    private void refreshPlayStutsUI() {
        switch (mStatus) {
            case STATUS_PLAY:
                mEZUIPlayerView.dismissomLoading();
                mPlayUI.mPlayImg.setImageResource(R.drawable.btn_stop_n);
                mPlayUI.mRecordImg.setEnabled(true);
                mPlayUI.mPictureImg.setEnabled(true);
                break;
            case STATUS_STOP:
                mPlayUI.mPlayImg.setImageResource(R.drawable.btn_play_n);
                mPlayUI.mRecordImg.setEnabled(false);
                mPlayUI.mPictureImg.setEnabled(false);
                break;
            case STATUS_PAUSE:
                mPlayUI.mPlayImg.setImageResource(R.drawable.btn_play_n);
                break;
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mEZPlayer != null) {
            mEZPlayer.setSurfaceHold(holder);
        }
        EZLog.d(TAG, "surfaceCreated   isInitSurface = " + isInitSurface + "  isPreparePlay = " + isPreparePlay + "    isResumePlay = "+isResumePlay);
        if (isInitSurface.compareAndSet(false,true) && isPreparePlay && isResumePlay.get()) {
            EZLog.d(TAG, "surfaceCreated   isInitSurface = " + isInitSurface + "  isPreparePlay = " + isPreparePlay + "    isResumePlay = "+isResumePlay);
            isResumePlay.set(false);
            prepareStartRealPlay();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isInitSurface.set(false);
    }

    @Override
    public void onBackPressed() {
        boolean isWide = mOrientationDetector.isWideScrren();
        if (isWide) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        if (mEZPlayer != null) {
            mEZPlayer.stopPlayback();
            mEZPlayer.release();
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EZLog.d(TAG, "onResume   mStatus = " + mStatus);
        mOrientationDetector.enable();
        //界面stop时，如果在播放，那isResumePlay标志位置为true，resume时恢复播放
        EZLog.d(TAG, "onResume   isInitSurface = " + isInitSurface + "   isPreparePlay = " + isPreparePlay);
        if (isInitSurface.get() && isPreparePlay && isResumePlay.get()) {
            isResumePlay.set(false);
            prepareStartRealPlay();
        }
    }


    @Override
    public void onClick(View view) {
        if (view == mBack) {
            onBackPressed();
        } else if (view == mFullScreenBtn) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (view == mEZUIPlayerView) {
            if (mOrientation == Configuration.ORIENTATION_LANDSCAPE && isSurfaceCanClick) {
                showOverlayBar(!isTopbarVisable());
            }
        } else if (view == mOverlayTopBarImgBack) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (view == mVoiceLeast) {
            mVolumeSeekBar.setProgress(0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        } else if (view == mVoiceLoudest) {
            mVolumeSeekBar.setProgress(mMaxVolume);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolume, 0);
            mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationDetector.disable();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop + " + mStatus);
        //界面stop时，如果在播放，那isResumePlay标志位置为true，以便resume时恢复播放
//         if (mStatus != RealPlayStatus.STATUS_STOP && mStatus != RealPlayStatus.STATUS_PAUSE) {
//            isResumePlay.set(true);
//        }
//        stopRealPlay();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        getWindow().getDecorView().setSystemUiVisibility(mUiOptions);
        mPlayBackPresenter.release();
        unRegisterVolumeReceiver();
        if (mEZPlayer != null) {
            mEZPlayer.release();
        }
        super.onDestroy();
    }


    public PlayBackActivity.EZOpenHandler mHandler = new PlayBackActivity.EZOpenHandler(this);


    @Override
    public void handlePrepareInfo() {
        refreshUI();
    }


    @Override
    public void handleSearchFileFormDeviceSuccess(EZDeviceRecordFile ezDeviceRecordFile) {
        isPreparePlay = true;
        mEZDeviceRecordFile = ezDeviceRecordFile;

        Calendar tmpStartTime = (mEZDeviceRecordFile.getStartTime());
        Calendar tmpEndTime = (mEZDeviceRecordFile.getStopTime());

        Log.v(TAG, "startTime:" + tmpStartTime.getTime() + " endTime:" + tmpEndTime.getTime());
        if (mStartTime.compareTo(tmpStartTime) >= 0 && mEndTime.compareTo(tmpEndTime) <= 0) {
            mEZDeviceRecordFile.setStartTime(mStartTime);
            mEZDeviceRecordFile.setStopTime(mEndTime);
            Log.d(TAG, "searchEZDeviceFileList success: start, " + mEZDeviceRecordFile.getStartTime());
        }
        mStartTimeTextView.setText(DateUtil.parseTimeToString(0));
        mEndTimeTextView.setText(DateUtil.parseTimeToString(mAlarmMaxDuration * 1000));
        prepareStartRealPlay();
    }

    @Override
    public void handleSearchFileFromCloudSuccess(EZCloudRecordFile ezCloudRecordFile) {
        isPreparePlay = true;
        mEZCloudRecordFile = ezCloudRecordFile;
        mStartTime = mEZCloudRecordFile.getStartTime();
        mEndTime = mEZCloudRecordFile.getStopTime();
        mStartTimeTextView.setText(DateUtil.parseTimeToString(0));
        mEndTimeTextView.setText(DateUtil.parseTimeToString(mAlarmMaxDuration * 1000));
        prepareStartRealPlay();
    }

    @Override
    public void handleSearchFileFail() {
        mEZUIPlayerView.showTipText(R.string.no_result_playback_file);
    }

    /**
     * 设置当前速率以及消耗总流量
     */
    private void updateRateFlow() {
        if (mEZPlayer == null && mStatus != STATUS_PLAY) {
            return;
        }
        long lastFlow = mEZPlayer.getStreamFlow();
        long streamFlowUnit = lastFlow - mStreamFlow;
        if (streamFlowUnit < 0)
            streamFlowUnit = 0;
        float fKBUnit = (float) streamFlowUnit / (float) EZOpenUtils.KB;
        String descUnit = String.format(getResources().getString(R.string.string_rate), String.format("%.2f k/s", fKBUnit));
        // 显示流量
        mRateTextView.setText(descUnit);
        mStreamFlow = lastFlow;
        mFlowTextView.setText(String.format(getResources().getString(R.string.string_flow), EZOpenUtils.transformToSize(mStreamFlow)));
    }

    /**
     * 设置当前录像时间
     */
    private void updateRecordTime() {
        if (mEZPlayer == null && mStatus != STATUS_PLAY) {
            return;
        }
        if (mIsRecording) {
            Calendar calendar = mEZPlayer.getOSDTime();
            if (calendar == null){
                return;
            }
            String time = DateUtil.OSD2Time(calendar);
            if (!TextUtils.equals(time, mLastOSDTime)) {
                mRecordTime++;
                mLastOSDTime = time;
            }
            mRecordTimeTextView.setText(DateUtil.getRecordTime(mRecordTime * 1000));
        }
    }

    /**
     * 设置当前回放进度
     */
    private void updateOSDTime() {
        if (mEZPlayer == null && mStatus != STATUS_PLAY) {
            return;
        }
        if (mEZPlayer == null && mStatus != STATUS_PLAY) {
            return;
        }
        mPlayTime = mEZPlayer.getOSDTime();
        if (mPlayTime == null) {
            return;
        }
        Log.d(TAG, "OSD = " + mPlayTime.getTimeInMillis());
        mPlaySeekBar.setProgress((int) (mPlayTime.getTimeInMillis() - mStartTime.getTimeInMillis()) / 1000);
        mStartTimeTextView.setText(DateUtil.parseTimeToString(mPlayTime.getTimeInMillis() - mStartTime.getTimeInMillis()));
    }

    private void startRealPlayUI() {
        mEZUIPlayerView.showLoading();
        mPlayUI.mPlayImg.setImageResource(R.drawable.btn_stop_n);
    }

    private void resumeRealPlayUI() {
        mPlayUI.mPlayImg.setImageResource(R.drawable.btn_stop_n);
        mEZUIPlayerView.dismissomLoading();
    }

    private void realStartPlay(String mVerifyCode) {
        if (!TextUtils.isEmpty(mVerifyCode)) {
            mEZPlayer.setPlayVerifyCode(mVerifyCode);
        }
        mStatus = STATUS_START;
        startRealPlayUI();
        if (mEZCloudRecordFile != null) {
            mEZPlayer.startPlayback(mEZCloudRecordFile);
        }else if (mEZDeviceRecordFile != null) {
            mEZPlayer.startPlayback(mEZDeviceRecordFile);
        }
        if (mPlayTime != null && mPlayTime.before(mEndTime) && mPlayTime.after(mStartTime)){
            mEZPlayer.seekPlayback(mPlayTime);
        }
    }

    /**
     * 准备开始播放
     */
    private void prepareStartRealPlay() {
        EZLog.d(TAG, "prepareStartRealPlay   isInitSurface = " + isInitSurface + "  isPreparePlay = " + isPreparePlay + "    isResumePlay = "+isResumePlay);
        if (!isInitSurface.get() || !isPreparePlay) {
            return;
        }
        if (mEZCloudRecordFile == null && mEZDeviceRecordFile == null) {
            return;
        }
        if (mStatus == STATUS_START || mStatus == STATUS_PLAY) {
            return;
        }
        //检测网络
        if (!EZOpenUtils.isNetworkAvailable(this)) {
            return;
        }

        if (mEZAlarmInfo.getIsEncrypt() == 0) {
            realStartPlay(null);
        } else {
            String verifyCode = mPlayBackPresenter.getDeviceEncrypt();
            if (!TextUtils.isEmpty(verifyCode)) {
                realStartPlay(verifyCode);
            } else {
                stopRealPlayUI();
                CommomAlertDialog.VerifyCodeInputDialog(PlayBackActivity.this, new CommomAlertDialog.VerifyCodeInputListener() {
                    @Override
                    public void onInputVerifyCode(String verifyCode) {
                        if (!TextUtils.isEmpty(verifyCode)) {
                            mVerifyCode = verifyCode;
                            mPlayBackPresenter.setDeviceEncrypt(mDeviceSerial, mVerifyCode);
                            realStartPlay(verifyCode);
                        }
                    }
                }).show();
            }
        }
    }

    /**
     * 开始播放
     */
    private void startRealPlay(String mVerifyCode) {
        if (!TextUtils.isEmpty(mVerifyCode)) {
            mEZPlayer.setPlayVerifyCode(mVerifyCode);
        }
        startRealPlayUI();
        if (mEZCloudRecordFile != null) {
            mEZPlayer.startPlayback(mEZCloudRecordFile);
            return;
        }
        if (mEZDeviceRecordFile != null) {
            mEZPlayer.startPlayback(mEZDeviceRecordFile);
            return;
        }
    }

    /**
     * 停止播放
     */
    private void stopRealPlayUI() {
        Log.d(TAG, "stopRealPlayUI");
        mHandler.removeMessages(MSG_REFRESH_PLAY_UI);
        mHandler.removeMessages(MSG_REFRESH_PLAY_PROGRESS);
        mRateTextView.setText(String.format(getResources().getString(R.string.string_rate), "0.0 k/s"));
        mStatus = STATUS_STOP;

        refreshPlayStutsUI();
    }

    /**
     * 停止播放
     */
    private void stopRealPlay() {
        Log.d(TAG, "stopRealPlay");
        stopRealPlayUI();
        if (mEZPlayer != null) {
            mEZPlayer.stopPlayback();
        }
    }


    /**
     * 停止播放
     */
    private void pauseRealPlay() {
        Log.d(TAG, "pauseRealPlay");
        mHandler.removeMessages(MSG_REFRESH_PLAY_UI);
        mHandler.removeMessages(MSG_REFRESH_PLAY_PROGRESS);
        mRateTextView.setText(String.format(getResources().getString(R.string.string_rate), "0.0 k/s"));
        mStatus = STATUS_PAUSE;
        if (mEZPlayer != null) {
            mEZPlayer.pausePlayback();
        }
        refreshPlayStutsUI();
    }

    /**
     * 恢复播放
     */
    private void resumeRealPlay() {
        Log.d(TAG, "resumeRealPlay");
        mHandler.sendEmptyMessage(MSG_REFRESH_PLAY_UI);
        mHandler.sendEmptyMessage(MSG_REFRESH_PLAY_PROGRESS);
        resumeRealPlayUI();
        mStatus = STATUS_PLAY;
        mEZPlayer.resumePlayback();
    }

    /**
     * 开启录像到手机
     */
    private void startRecord() {
        EZLog.debugLog(TAG, "startRecord");
        if (mEZPlayer == null) {
            Log.d(TAG, "EZPlaer is null");
            return;
        }
        if (mIsRecording) {
            stopRecord();
            return;
        }
        if (!EZOpenUtils.isSDCardUseable()) {
            // 提示SD卡不可用
            showToast(R.string.remoteplayback_SDCard_disable_use);
            return;
        }
        if (EZOpenUtils.getSDCardRemainSize() < EZOpenUtils.PIC_MIN_MEM_SPACE) {
            // 提示内存不足
            showToast(R.string.remoteplayback_record_fail_for_memory);
            return;
        }
        mRecordPath = DataManager.getRecordFile();
        if (mEZPlayer != null) {
            EZOpenUtils.soundPool(PlayBackActivity.this, R.raw.record);
            if (mEZPlayer.startLocalRecordWithFile(mRecordPath)) {
                handleRecordSuccess();
            } else {
                handleRecordFail();
            }
        }
    }

    /**
     * 停止录像
     *
     * @see
     * @since V1.0
     */
    private void stopRecord() {
        if (mEZPlayer == null || !mIsRecording) {
            return;
        }
        showToast(mRecordPath);
        EZOpenUtils.soundPool(PlayBackActivity.this, R.raw.record);
        mEZPlayer.stopLocalRecord();
        // 计时按钮不可见
        mRecordLayout.setVisibility(View.GONE);
        mPlayUI.mRecordImg.setImageResource(R.drawable.btn_record_selector);
        mIsRecording = false;
    }


    /**
     * 处理播放成功的情况
     */
    private void handlePlaySuccess() {
        mPlaySeekBar.setEnabled(true);
        mStatus = STATUS_PLAY;
        refreshPlayStutsUI();
        // 声音处理
        setRealPlaySound();
        mHandler.sendEmptyMessage(MSG_REFRESH_PLAY_UI);
    }

    /**
     * 处理播放成功的情况
     */
    private void handleVideoSizeChange(int width, int height) {
        mEZUIPlayerView.setVideoSizeChange(width, height);
        mPlaySeekBar.setEnabled(true);
        mStatus = STATUS_PLAY;
        refreshPlayStutsUI();
        // 声音处理
        setRealPlaySound();
        mHandler.sendEmptyMessage(MSG_REFRESH_PLAY_UI);
    }

    /**
     * 处理播放失败的情况
     */
    private void handlePlayFail(BaseException e) {
        if (mStatus == STATUS_START) {
            mStatus = STATUS_STOP;
            mEZUIPlayerView.dismissomLoading();
            stopRealPlay();
            updateRealPlayFailUI(e.getErrorCode());
        }
    }

    /**
     * 处理播放完成的情况
     */
    private void handlePlayFinish() {
        // TODO: 2017/2/23  处理播放完成的情况
        mPlaySeekBar.setProgress(mAlarmMaxDuration);
        mPlayTime = mEndTime;
        mStartTimeTextView.setText(DateUtil.parseTimeToString(mAlarmMaxDuration * 1000));
        stopRealPlay();
    }

    private void updateRealPlayFailUI(int errorCode) {
        String txt = null;
        Log.i(TAG, "updateRealPlayFailUI: errorCode:" + errorCode);
        // 判断返回的错误码
        switch (errorCode) {
            case ErrorCode.ERROR_TRANSF_ACCESSTOKEN_ERROR:
                EZOpenUtils.gotoLogin();
                return;
            case ErrorCode.ERROR_CAS_MSG_PU_NO_RESOURCE:
                txt = getString(R.string.remoteplayback_over_link);
                break;
            case ErrorCode.ERROR_TRANSF_DEVICE_OFFLINE:
                txt = getString(R.string.realplay_fail_device_not_exist);
                break;
            case ErrorCode.ERROR_INNER_STREAM_TIMEOUT:
                txt = getString(R.string.realplay_fail_connect_device);
                break;
            case ErrorCode.ERROR_WEB_CODE_ERROR:
                //VerifySmsCodeUtil.openSmsVerifyDialog(Constant.SMS_VERIFY_LOGIN, this, this);
                //txt = Utils.getErrorTip(this, R.string.check_feature_code_fail, errorCode);
                break;
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_OP_ERROR:
                //VerifySmsCodeUtil.openSmsVerifyDialog(Constant.SMS_VERIFY_HARDWARE, this, null);
//                SecureValidate.secureValidateDialog(this, this);
                //txt = Utils.getErrorTip(this, R.string.check_feature_code_fail, errorCode);
                break;
            case ErrorCode.ERROR_TRANSF_TERMINAL_BINDING:
                txt = "请在萤石客户端关闭终端绑定";
                break;
            // 收到这两个错误码，可以弹出对话框，让用户输入密码后，重新取流预览
            case ErrorCode.ERROR_INNER_VERIFYCODE_NEED:
            case ErrorCode.ERROR_INNER_VERIFYCODE_ERROR: {

                CommomAlertDialog.VerifyCodeInputDialog(PlayBackActivity.this, new CommomAlertDialog.VerifyCodeInputListener() {
                    @Override
                    public void onInputVerifyCode(String verifyCode) {
                        if (!TextUtils.isEmpty(verifyCode)) {
                            mVerifyCode = verifyCode;
                            mPlayBackPresenter.setDeviceEncrypt(mDeviceSerial, mVerifyCode);
                            startRealPlay(mVerifyCode);
                        }
                    }
                }).show();
            }
            break;
            case ErrorCode.ERROR_EXTRA_SQUARE_NO_SHARING:
            default:
                txt = getErrorTip(R.string.realplay_play_fail, errorCode);
                break;
        }

        if (!TextUtils.isEmpty(txt)) {
            setRealPlayFailUI(txt);
        } else {
            setRealPlayStopUI();
        }
    }

    private void setRealPlayFailUI(String txt) {
        mEZUIPlayerView.showTipText(txt);
    }

    private void setRealPlayStopUI() {

    }

    /**
     * 开始录像成功
     */
    private void handleRecordSuccess() {
        mIsRecording = true;
        // 计时按钮可见
        mRecordLayout.setVisibility(View.VISIBLE);
        mRecordTimeTextView.setText("00:00");
        mPlayUI.mRecordImg.setImageResource(R.drawable.resource_selected);
        mRecordTime = 0;
    }

    /**
     * 开始录像失败
     */
    private void handleRecordFail() {
        showToast(R.string.remoteplayback_record_fail);
        if (mIsRecording) {
            stopRecord();
        }
    }

    /**
     * 设置播放是否开启音频
     */
    private void setRealPlaySound() {
        if (mEZPlayer != null) {
            if (isSoundOpen) {
                mEZPlayer.openSound();
            } else {
                mEZPlayer.closeSound();
            }
        }
    }

    private static class EZOpenHandler extends Handler {
        WeakReference<Activity> mActivity;

        EZOpenHandler(Activity mActivity) {
            this.mActivity = new WeakReference<Activity>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayBackActivity ower = (PlayBackActivity) mActivity.get();
            if (ower == null || ower.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case MSG_REFRESH_PLAY_UI:
                    removeMessages(MSG_REFRESH_PLAY_UI);
                    ower.updateRateFlow();
                    ower.updateRecordTime();
                    sendEmptyMessageDelayed(MSG_REFRESH_PLAY_UI, 1000);
                    break;
                case MSG_REFRESH_PLAY_PROGRESS:
                    ower.updateOSDTime();
                    sendEmptyMessageDelayed(MSG_REFRESH_PLAY_PROGRESS, 1000);
                    break;
                case MSG_HIDE_TOPBAR:
                    ower.showOverlayBar(false);
                    break;
                case MSG_SHOW_TOPBAR:
                    ower.showOverlayBar(true);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 注册当音量发生变化时接收的广播
     */
    private void registerVolumeReceiver() {
        mVolumeReceiver = new PlayBackActivity.VolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mVolumeReceiver, filter);
    }

    /**
     * 注册当音量发生变化时接收的广播
     */
    private void unRegisterVolumeReceiver() {
        if (mVolumeReceiver != null) {
            unregisterReceiver(mVolumeReceiver);
        }
    }

    private boolean isTopbarVisable() {
        return mOverlayTopBar.geTopMargin() >= 0;
    }

    private void showOverlayBar(boolean show) {
        int duration = 300;
        if (show) {
            if (!isTopbarVisable()) {
                ObjectAnimator ob = ObjectAnimator.ofFloat(this.mOverlayTopBar, "topMargin", 1f, 0f);
                ob.setDuration(duration);
                animSetXY.playTogether(ob);
                animSetXY.start();
                mHandler.removeMessages(MSG_HIDE_TOPBAR);
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_TOPBAR, SHOW_TOP_BAR_TIME);
            }
        } else {
            if (isTopbarVisable()) {
                ObjectAnimator ob = ObjectAnimator.ofFloat(this.mOverlayTopBar, "topMargin", 0f, 1f);
                ob.setDuration(duration);
                animSetXY.playTogether(ob);
                animSetXY.start();
                mHandler.removeMessages(MSG_SHOW_TOPBAR);
                mHandler.removeMessages(MSG_HIDE_TOPBAR);
            }
        }
    }

    private void setHideAnimation(final View view, int duration) {
        if (null == view || duration < 0) {
            return;
        }
        if (null == mHideAnimation) {
            mHideAnimation = new AlphaAnimation(1.0f, 0.0f);
            mHideAnimation.setDuration(duration);
        }
        view.startAnimation(mHideAnimation);
        view.setVisibility(View.GONE);
    }

    private void setShowAnimation(final View view, int duration) {
        if (null == view || duration < 0) {
            return;
        }
        if (null == mShowAnimation) {
            mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
            mShowAnimation.setDuration(duration);
        }
        view.startAnimation(mShowAnimation);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * 处理音量变化时的界面显示
     *
     * @author long
     */
    private class VolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果音量发生变化则更改seekbar的位置
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前的媒体音量
                mVolumeSeekBar.setProgress(currVolume);
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setOrientation(int orientation) {
        mOrientation = orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(mUiOptions);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(mUiOptions);
        }
        setSurfaceSize();
//      fillSurfaceWidth(mSurface, mVideoWidth, mVideoHeight);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setOrientation(newConfig.orientation);
    }

    protected void setSurfaceSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        boolean isWideScrren = mOrientationDetector.isWideScrren();
        if (!isWideScrren) {
            mEZUIPlayerView.setSurfaceSize(dm.widthPixels, 0);
            mOverlayTopBar.setVisibility(View.GONE);
            mTopBar.setVisibility(View.VISIBLE);
        } else {
            mEZUIPlayerView.setSurfaceSize(dm.widthPixels, dm.heightPixels);
            mOverlayTopBar.setVisibility(View.VISIBLE);
            showOverlayBar(true);
            mTopBar.setVisibility(View.GONE);
            mHandler.removeMessages(MSG_HIDE_TOPBAR);
            mHandler.removeMessages(MSG_SHOW_TOPBAR);
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_TOPBAR, SHOW_TOP_BAR_TIME);
        }
    }

    @Override
    public void onWindowSizeChanged(int w, int h, int oldW, int oldH) {
        EZLog.d(TAG, "w = " + w + ",h = " + h + ",oldW = " + oldW + ",oldH = " + oldH);
        if (mEZUIPlayerView != null && h != 0) {
            setSurfaceSize();
        }

    }

    public class MyOrientationDetector extends OrientationEventListener {

        private WindowManager mWindowManager;
        private int mLastOrientation = 0;

        public MyOrientationDetector(Context context) {
            super(context);
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        public boolean isWideScrren() {
            Display display = mWindowManager.getDefaultDisplay();
            Point pt = new Point();
            display.getSize(pt);
            return pt.x > pt.y;
        }

        @Override
        public void onOrientationChanged(int orientation) {
            int value = getCurentOrientationEx(orientation);
            if (value != mLastOrientation) {
                mLastOrientation = value;
                int current = getRequestedOrientation();
                if (current == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        || current == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }
        }

        private int getCurentOrientationEx(int orientation) {
            int value = 0;
            if (orientation >= 315 || orientation < 45) {
                // 0度
                value = 0;
                return value;
            }
            if (orientation >= 45 && orientation < 135) {
                // 90度
                value = 90;
                return value;
            }
            if (orientation >= 135 && orientation < 225) {
                // 180度
                value = 180;
                return value;
            }
            if (orientation >= 225 && orientation < 315) {
                // 270度
                value = 270;
                return value;
            }
            return value;
        }
    }
}


