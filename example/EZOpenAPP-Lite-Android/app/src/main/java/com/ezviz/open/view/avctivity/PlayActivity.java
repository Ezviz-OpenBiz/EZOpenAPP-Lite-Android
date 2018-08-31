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
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.common.WindowSizeChangeNotifier;
import com.ezviz.open.model.EZOpenVideoQualityInfo;
import com.ezviz.open.presenter.PlayPresenter;
import com.ezviz.open.utils.DataManager;
import com.ezviz.open.utils.DateUtil;
import com.ezviz.open.utils.EZLog;
import com.ezviz.open.utils.EZOpenUtils;
import com.ezviz.open.view.PlayView;
import com.ezviz.open.view.widget.CommomAlertDialog;
import com.ezviz.open.view.widget.EZUIPlayerView;
import com.ezviz.open.view.widget.ExRelativeLayout;

import com.ezviz.open.view.widget.RingView;
import com.ezviz.opensdk.base.data.DeviceManager;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.EZTalk;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.RealmList;
import com.ezviz.open.R;
import com.videogo.openapi.OnEZPlayerCallBack;
import com.videogo.openapi.OnEZTalkCallBack;

/**
 * Description:视频播放界面
 * Created by dingwei3
 *
 * @date : 2016/12/21
 */
public class PlayActivity extends RootActivity implements SurfaceHolder.Callback, PlayView, View.OnClickListener, WindowSizeChangeNotifier.OnWindowSizeChangedListener, Animator.AnimatorListener {

    private static final String TAG = "PlayActivity";


    public final static int STATUS_INIT = 1;
    public final static int STATUS_START = 2;
    public final static int STATUS_PLAY = 3;
    public final static int STATUS_STOP = 4;


    /**
     * 播放器状态
     */
    public int mStatus = STATUS_INIT;


    private static final int MSG_REFRESH_PLAY_UI = 1000;
    private static final int MSG_HIDE_TOPBAR = 1001;
    private static final int MSG_SHOW_TOPBAR = 1002;
    private static final int SHOW_TOP_BAR_TIME = 5000;


    private PlayPresenter mPlayPresenter;
    private EZUIPlayerView mEZUIPlayerView;
    private ImageView mBack;

    private EZPlayer mEZPlayer;
    private EZTalk mEZTalk;
    private String mDeviceSerial;
    private int mCameraNo = -1;
    private int mOrientation;
    private String mVerifyCode;
    private TextView mTitleTextView;
    private TextView mDescTextView;
    private ImageView mFullScreenBtn;

    private PlayUI mPlayUI;

    private boolean isSoundOpen = true;
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private int mCurrentVolume;
    private AppCompatSeekBar mVolumeSeekBar;
    private VolumeReceiver mVolumeReceiver;
    private TextView mRateTextView;
    private TextView mFlowTextView;
    private RelativeLayout mTopBar;
    private LinearLayout mRecordLayout;
    private RelativeLayout mPlayContorlLayout;
    private RelativeLayout mPlayLayout;
    private ImageView mRecordingImg;
    private TextView mRecordTimeTextView;
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
    private MyOrientationDetector mOrientationDetector;
    /**
     * 横屏模式下顶部topbar
     */
    private ExRelativeLayout mOverlayTopBar;
    private ImageView mOverlayTopBarImgBack;

    private AlphaAnimation mHideAnimation;
    private AlphaAnimation mShowAnimation;
    private AnimatorSet animSetXY;
    private boolean isSurfaceCanClick = true;
    /**
     * resume时是否恢复播放
     */
    private AtomicBoolean isResumePlay = new AtomicBoolean(true);

    /**
     * surface是否创建好
     */
    private AtomicBoolean isInitSurface = new AtomicBoolean(false);

    private int mVideoWidth;
    private int mVideoHeight;

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
         * 对讲
         */
        ImageView mTalkImg;
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
        /**
         * 设置
         */
        ImageView mSettingImg;
//        /**
//         * 均衡
//         */
//        TextView mBalanced;
//        /**
//         * 流畅
//         */
//        TextView mFlunet;
//        /**
//         * 高清
//         */
//        TextView mHD;

        LinearLayout mLinearLayout;

        TextView[] mTextViews;

        @Override
        public void onClick(View view) {
            if (mPlayImg == view) {
                if (mStatus == STATUS_PLAY) {
                    stopRealPlay();
                } else if (mStatus == STATUS_STOP) {
                    startRealPlay();
                }
            } else if (mPictureImg == view) {
                mPlayPresenter.savePicture(PlayActivity.this, mEZPlayer);
            } else if (mTalkImg == view) {
                startVoiceTalk();
            } else if (mRecordImg == view) {
                startRecord();
            } else if (mSettingImg == view) {
                DeviceSettingActivity.startDeviceSetting(PlayActivity.this, mDeviceSerial);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 保持屏幕常亮
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mPlayPresenter = new PlayPresenter(this);
        initView();
        initData();
        registerVolumeReceiver();
        setSurfaceSize();
    }

    private void initView() {
        mEZUIPlayerView = (EZUIPlayerView) findViewById(R.id.play_view);
        mEZUIPlayerView.setOnClickListener(this);
        mEZUIPlayerView.setSurfaceHolderCallback(PlayActivity.this);
        mTopBar = (RelativeLayout) findViewById(R.id.play_topbar);
        mVolumeSeekBar = (AppCompatSeekBar) findViewById(R.id.volume_seekbar);
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
        mPlayContorlLayout = (RelativeLayout) findViewById(R.id.main_play_layout);
        mPlayLayout = (RelativeLayout) findViewById(R.id.play_layout);
        mVoiceLeast = (ImageView) findViewById(R.id.btn_voice_least);
        mVoiceLoudest = (ImageView) findViewById(R.id.btn_voice_loudest);
        mVoiceLeast.setOnClickListener(this);
        mVoiceLoudest.setOnClickListener(this);
        mOverlayTopBarImgBack.setOnClickListener(this);
        mFullScreenBtn.setOnClickListener(this);
        animSetXY = new AnimatorSet();
        animSetXY.addListener(this);
        initPlayUI();
    }

    /**
     * 初始化播放控制相关UI
     */
    private void initPlayUI() {
        mPlayUI = new PlayUI();
        mPlayUI.mTalkImg = (ImageView) findViewById(R.id.img_talk);
        mPlayUI.mRecordImg = (ImageView) findViewById(R.id.img_record);
        mPlayUI.mPlayImg = (ImageView) findViewById(R.id.img_play);
        mPlayUI.mPictureImg = (ImageView) findViewById(R.id.img_picture);
        mPlayUI.mSettingImg = (ImageView) findViewById(R.id.img_more_setting);
        mPlayUI.mLinearLayout = (LinearLayout) findViewById(R.id.video_level_layout);
        mPlayUI.mPlayImg.setOnClickListener(mPlayUI);
        mPlayUI.mPictureImg.setOnClickListener(mPlayUI);
        mPlayUI.mTalkImg.setOnClickListener(mPlayUI);
        mPlayUI.mRecordImg.setOnClickListener(mPlayUI);
        mPlayUI.mSettingImg.setOnClickListener(mPlayUI);
        mPlayUI.mRecordImg.setEnabled(false);
        mPlayUI.mPictureImg.setEnabled(false);
    }

    private void initData() {
        Intent intent = getIntent();
        mDeviceSerial = intent.getStringExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL);
        mCameraNo = intent.getIntExtra(EZOpenConstant.EXTRA_CAMERA_NO, -1);

        if (TextUtils.isEmpty(mDeviceSerial) || mCameraNo == -1) {
            EZLog.d(TAG, "mDeviceSerial or mCameraNo is null");
            finish();
            return;
        }
        mEZPlayer = EZPlayer.createPlayer(mDeviceSerial, mCameraNo);
        mEZTalk = EZTalk.createEZTalk(mDeviceSerial,mCameraNo);
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
            }
        });
        mEZTalk.setOnEZTalkCallBack(new OnEZTalkCallBack() {
            @Override
            public void onTalkSuccess() {
                EZLog.d(TAG, "onTalkSuccess");
                handleVoiceTalkSucceed();
            }

            @Override
            public void onTalkFailed(BaseException e) {
                EZLog.d(TAG, "onTalkFailed");
                handleVoiceTalkFailed(e);
            }

            @Override
            public void onTalkStoped() {

            }
        });
        mPlayPresenter.prepareInfo(mDeviceSerial, mCameraNo);
        mOrientationDetector = new MyOrientationDetector(this);
        mUiOptions = getWindow().getDecorView().getSystemUiVisibility();
        new WindowSizeChangeNotifier(this, this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Bitmap bitmap = mPlayPresenter.blurBitmap(this, 25f);
        if (bitmap != null) {
            ((ImageView) findViewById(R.id.img_main)).setImageBitmap(bitmap);
        }
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
        if (mPlayPresenter.getOpenDeviceInfo() != null && mPlayPresenter.getOpenCameraInfo() != null) {
            if (mPlayPresenter.getOpenDeviceInfo().getStatus() == 2) {
                // TODO: 2016/12/28 不在线处理
                mEZUIPlayerView.showTipText(R.string.realplay_fail_device_not_exist);
            } else {
                // TODO: 2016/12/28 设备在线处理

            }
            mTitleTextView.setText(mPlayPresenter.getOpenCameraInfo().getCameraName());
            mDescTextView.setText(mPlayPresenter.getOpenDeviceInfo().getDisplayDeviceType());
            initQualityUI(mPlayPresenter.getOpenCameraInfo().getEZOpenVideoQualityInfos());
            refreshQualityUI(mPlayPresenter.getOpenCameraInfo().getVideoLevel());
        }
    }

    private void initQualityUI(RealmList<EZOpenVideoQualityInfo> list) {
        if (list != null && list.isValid() && mPlayUI.mTextViews == null) {
            mPlayUI.mTextViews = new TextView[list.size()];
            for (int i = 0; i < list.size(); i++) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                relativeLayout.setLayoutParams(layoutParams);

                TextView textView = new TextView(this);
                textView.setBackgroundResource(R.drawable.btn_video_level_selector);
                textView.setTextColor(getResources().getColor(R.color.color_white));
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setText(list.get(i).getVideoQualityName());
                textView.setTag(list.get(i).getVideoLevel());
                mPlayUI.mTextViews[i] = textView;
                textView.setOnClickListener(mOnVideoQualityClickListener);
                relativeLayout.addView(textView);
                mPlayUI.mLinearLayout.addView(relativeLayout);
            }
        }
    }

    private View.OnClickListener mOnVideoQualityClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mPlayPresenter.setQuality(mDeviceSerial, mCameraNo, ((Integer) view.getTag()));
        }
    };

    /**
     * 更新清晰度显示UI
     */
    private void refreshQualityUI(int level) {
        if (mPlayUI.mTextViews != null) {
            for (int i = 0; i < mPlayUI.mTextViews.length; i++) {
                if (((Integer) (mPlayUI.mTextViews[i].getTag())) == level) {
                    mPlayUI.mTextViews[i].setSelected(true);
                } else {
                    mPlayUI.mTextViews[i].setSelected(false);
                }
            }
        }
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
                mEZUIPlayerView.dismissomLoading();
                mPlayUI.mPlayImg.setImageResource(R.drawable.btn_play_n);
                mPlayUI.mRecordImg.setEnabled(false);
                mPlayUI.mPictureImg.setEnabled(false);
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
        EZLog.d(TAG, "surfaceCreated   isInitSurface = " + isInitSurface);
        if (isInitSurface.compareAndSet(false,true) && isResumePlay.get()) {
            isResumePlay.set(false);
            startRealPlay();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        EZLog.d(TAG, "surfaceDestroyed");
        isInitSurface.set(false);
    }

    @Override
    public void onBackPressed() {
        boolean isWide = mOrientationDetector.isWideScrren();
        if (isWide) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        if (mIsOnTalk) {
            if (mTalkPopupWindow != null) {
                Log.i(TAG, "closeTalkPopupWindow");
                mTalkPopupWindow.dismiss();
                mTalkPopupWindow = null;
            }
            stopVoiceTalk();
            mTalkRingView = null;
            return;
        }
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
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
        EZLog.d(TAG, "onResume   isInitSurface = " + isInitSurface +"   isResumePlay = "+isResumePlay);
        if (isResumePlay.get() && isInitSurface.get()) {
            isResumePlay.set(false);
            EZLog.d(TAG, "onResume   isInitSurface = " + isInitSurface);
            startRealPlay();
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
        super.onStop();
        closeTalkPopupWindow(true);
        Log.d(TAG, "onStop + " + mStatus);
        //界面stop时，如果在播放，那isResumePlay标志位置为true，以便resume时恢复播放
        if (mStatus != STATUS_STOP) {
            isResumePlay.set(true);
        }
        stopRealPlay();
    }

    @Override
    protected void onDestroy() {
        getWindow().getDecorView().setSystemUiVisibility(mUiOptions);
        mPlayPresenter.release();
        unRegisterVolumeReceiver();
        if (mEZPlayer != null) {
            mEZPlayer.release();
        }
        super.onDestroy();
    }


    public EZOpenHandler mHandler = new EZOpenHandler(this);

    @Override
    public void handleEZOpenDeviceInfo() {
    }

    @Override
    public void handleEZOpenCameraInfo() {
        refreshUI();
    }

    @Override
    public void handlePrepareInfo() {
        refreshUI();
    }


    @Override
    public void handleSetQualitSuccess() {
        // TODO: 2017/1/4  设置清晰度成功
        reStartPlay();
    }

    /**
     * 重启播放
     */
    private void reStartPlay() {
        if (mStatus == STATUS_PLAY) {
            // 停止播放
            stopRealPlay();
            //下面语句防止stopRealPlay线程还没释放surface, startRealPlay线程已经开始使用surface
            //因此需要等待500ms
            SystemClock.sleep(500);
            // 开始播放
            startRealPlay();
        }
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

    private void startRealPlayUI() {
        mEZUIPlayerView.showLoading();
        mPlayUI.mPlayImg.setImageResource(R.drawable.btn_stop_n);
    }

    private void realStartPlay(String mVerifyCode) {
        if (!TextUtils.isEmpty(mVerifyCode)) {
            mEZPlayer.setPlayVerifyCode(mVerifyCode);
        }
        mStatus = STATUS_START;
        startRealPlayUI();
        mEZPlayer.startRealPlay();
    }

    /**
     * 开始播放
     */
    private void startRealPlay() {
        Log.d(TAG, "startRealPlay  mStatus = " + mStatus);
        if (mStatus == STATUS_START || mStatus == STATUS_PLAY) {
            return;
        }
        //检测网络
        if (!EZOpenUtils.isNetworkAvailable(this)) {
            return;
        }
        //设备信息为空
        if (mPlayPresenter.getOpenDeviceInfo() == null || mPlayPresenter.getOpenCameraInfo() == null) {
            return;
        }
        if (mPlayPresenter.getOpenDeviceInfo().getIsEncrypt() == 0) {
            realStartPlay(null);
        } else {
            String verifyCode = mPlayPresenter.getDeviceEncrypt();
            if (!TextUtils.isEmpty(verifyCode)) {
                realStartPlay(verifyCode);
            } else {
                stopRealPlayUI();
                CommomAlertDialog.VerifyCodeInputDialog(PlayActivity.this, new CommomAlertDialog.VerifyCodeInputListener() {
                    @Override
                    public void onInputVerifyCode(String verifyCode) {
                        if (!TextUtils.isEmpty(verifyCode)) {
                            mVerifyCode = verifyCode;
                            mPlayPresenter.setDeviceEncrypt(mDeviceSerial, mVerifyCode);
                            realStartPlay(mVerifyCode);
                        }
                    }
                }).show();
            }
        }
    }

    /**
     * 停止播放
     */
    private void stopRealPlay() {
        stopRealPlayUI();
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
        }
    }

    /**
     * 停止播放UI
     */
    private void stopRealPlayUI() {
        Log.d(TAG, "stopRealPlay");
        mHandler.removeMessages(MSG_REFRESH_PLAY_UI);
        mRateTextView.setText(String.format(getResources().getString(R.string.string_rate), "0.0 k/s"));
        mStatus = STATUS_STOP;
        refreshPlayStutsUI();
    }

    /**
     * 设备对讲
     */
    private void startVoiceTalk() {
        EZLog.debugLog(TAG, "startVoiceTalk");
        if (mEZPlayer == null) {
            Log.d(TAG, "EZPlaer is null");
            return;
        }
        mIsOnTalk = true;
        showToast(R.string.start_voice_talk);
        if (mEZPlayer != null) {
            mEZPlayer.closeSound();
        }
        mEZTalk.startVoiceTalk();
    }

    /**
     * 停止对讲
     */
    private void stopVoiceTalk() {
        if (mPlayPresenter.getOpenCameraInfo() == null || !mPlayPresenter.getOpenCameraInfo().isValid() || mEZPlayer == null) {
            return;
        }
        EZLog.debugLog(TAG, "stopVoiceTalk");
        mEZTalk.stopVoiceTalk();
        handleVoiceTalkStoped();
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
            EZOpenUtils.soundPool(PlayActivity.this, R.raw.record);
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
        EZOpenUtils.soundPool(PlayActivity.this, R.raw.record);
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
        mStatus = STATUS_PLAY;
        refreshPlayStutsUI();
        // 声音处理
        setRealPlaySound();
        if (mPlayPresenter.getOpenDeviceInfo() != null && mPlayPresenter.getOpenDeviceInfo().supportTalkMode() != 0) {
            mPlayUI.mTalkImg.setEnabled(true);
        } else {
            mPlayUI.mTalkImg.setEnabled(false);
        }
        mHandler.sendEmptyMessage(MSG_REFRESH_PLAY_UI);
    }

    /**
     * 处理播放失败的情况
     */
    private void handlePlayFail(BaseException e) {
        if (mStatus != STATUS_STOP) {
            mStatus = STATUS_STOP;
            mEZUIPlayerView.dismissomLoading();
            stopRealPlay();
            updateRealPlayFailUI(e.getErrorCode());
        }
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
                CommomAlertDialog.VerifyCodeInputDialog(PlayActivity.this, new CommomAlertDialog.VerifyCodeInputListener() {
                    @Override
                    public void onInputVerifyCode(String verifyCode) {
                        if (!TextUtils.isEmpty(verifyCode)) {
                            mVerifyCode = verifyCode;
                            mPlayPresenter.setDeviceEncrypt(mDeviceSerial, mVerifyCode);
                            realStartPlay(mVerifyCode);
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

    /**
     * 开启对讲成功
     */
    private void handleVoiceTalkSucceed() {
        openTalkPopupWindow(true);
        mPlayUI.mTalkImg.setEnabled(false);
    }

    /**
     * 开启对讲失败
     * @param exception
     */
    private void handleVoiceTalkFailed(BaseException exception) {
        EZLog.debugLog(TAG, "Talkback failed. " + exception.toString());
        closeTalkPopupWindow(true);
        switch (exception.getErrorCode()) {
            case ErrorCode.ERROR_TRANSF_DEVICE_TALKING:
                showToast(R.string.realplay_play_talkback_fail_ison);
                break;
            case ErrorCode.ERROR_TRANSF_DEVICE_PRIVACYON:
                showToast(R.string.realplay_play_talkback_fail_privacy);
                break;
            case ErrorCode.ERROR_TRANSF_DEVICE_OFFLINE:
                showToast(R.string.realplay_fail_device_not_exist);
                break;
            case ErrorCode.ERROR_TTS_MSG_REQ_TIMEOUT:
            case ErrorCode.ERROR_TTS_MSG_SVR_HANDLE_TIMEOUT:
            case ErrorCode.ERROR_TTS_WAIT_TIMEOUT:
            case ErrorCode.ERROR_TTS_HNADLE_TIMEOUT:
                showToast(R.string.realplay_play_talkback_request_timeout, exception.getErrorCode());
                break;
            case ErrorCode.ERROR_CAS_AUDIO_SOCKET_ERROR:
            case ErrorCode.ERROR_CAS_AUDIO_RECV_ERROR:
            case ErrorCode.ERROR_CAS_AUDIO_SEND_ERROR:
                showToast(R.string.realplay_play_talkback_network_exception, exception.getErrorCode());
                break;
            default:
                showToast(R.string.realplay_play_talkback_fail, exception.getErrorCode());
                break;
        }
    }

    private void handleVoiceTalkStoped() {
        if (mIsOnTalk) {
            mIsOnTalk = false;
        }
        mPlayUI.mTalkImg.setEnabled(true);
        if (mStatus == STATUS_PLAY) {
            if (mEZPlayer != null) {
                if (isSoundOpen) {
                    mEZPlayer.openSound();
                } else {
                    mEZPlayer.closeSound();
                }
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
            PlayActivity ower = (PlayActivity) mActivity.get();
            if (ower == null || ower.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case MSG_REFRESH_PLAY_UI:
                    EZLog.d(TAG, "MSG_REFRESH_PLAY_UI");
                    removeMessages(MSG_REFRESH_PLAY_UI);
                    ower.updateRateFlow();
                    ower.updateRecordTime();
                    sendEmptyMessageDelayed(MSG_REFRESH_PLAY_UI, 1000);
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


    PopupWindow mTalkPopupWindow;
    RingView mTalkRingView;
    Button mTalkBackControlBtn;
    boolean mIsOnTalk;

    /**
     * 打开对讲控制窗口
     */
    private void openTalkPopupWindow(boolean showAnimation) {
        if (mEZPlayer == null && mPlayPresenter.getOpenDeviceInfo() == null && mPlayPresenter.getOpenDeviceInfo().isValid()) {
            return;
        }
        closeTalkPopupWindow(false);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layoutView = (ViewGroup) layoutInflater.inflate(R.layout.realplay_talkback_wnd, null, true);
        layoutView.setFocusable(true);
        layoutView.setFocusableInTouchMode(true);
        ImageButton talkbackCloseBtn = (ImageButton) layoutView.findViewById(R.id.talkback_close_btn);
        talkbackCloseBtn.setOnClickListener(mOnPopWndClickListener);
        mTalkRingView = (RingView) layoutView.findViewById(R.id.talkback_rv);
        mTalkBackControlBtn = (Button) layoutView.findViewById(R.id.talkback_control_btn);
        mTalkBackControlBtn.setOnTouchListener(mOnTouchListener);
        //对讲全双工模式
        if (mPlayPresenter.getOpenDeviceInfo() != null && mPlayPresenter.getOpenDeviceInfo().getSupportTalkValue() == 1) {
            mTalkRingView.setVisibility(View.VISIBLE);
            mTalkBackControlBtn.setEnabled(false);
            mTalkBackControlBtn.setText(R.string.talking);
        }
        int height = mPlayContorlLayout.getHeight();
        mTalkPopupWindow = new PopupWindow(layoutView, RelativeLayout.LayoutParams.MATCH_PARENT, height, true);
        if (showAnimation) {
            mTalkPopupWindow.setAnimationStyle(R.style.popwindowUpAnim);
        }
        mTalkPopupWindow.setFocusable(false);
        mTalkPopupWindow.setOutsideTouchable(false);
        mTalkPopupWindow.showAsDropDown(mPlayLayout);
        mTalkPopupWindow.update();
        mTalkRingView.post(new Runnable() {
            @Override
            public void run() {
                if (mTalkRingView != null) {
                    mTalkRingView.setMinRadiusAndDistance(mTalkBackControlBtn.getHeight() / 2f,
                            EZOpenUtils.dip2px(PlayActivity.this, 22));
                }
            }
        });
    }

    /**
     * 关闭对讲窗口
     *
     * @param stopTalk
     */
    private void closeTalkPopupWindow(boolean stopTalk) {
        if (mTalkPopupWindow != null) {
            EZLog.infoLog(TAG, "closeTalkPopupWindow");
            mTalkPopupWindow.dismiss();
            mTalkPopupWindow = null;
        }
        mTalkRingView = null;
        if (stopTalk)
            stopVoiceTalk();
    }


    private View.OnClickListener mOnPopWndClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.talkback_close_btn:
                    closeTalkPopupWindow(true);
                    break;
                default:
                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionevent) {
            boolean ptz_result = false;
            int action = motionevent.getAction();
            final int speed = EZConstants.PTZ_SPEED_DEFAULT;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    switch (view.getId()) {
                        case R.id.talkback_control_btn:
                            mTalkRingView.setVisibility(View.VISIBLE);
                            mEZTalk.setVoiceTalkStatus(true);
                            break;
                        default:
                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    switch (view.getId()) {
                        case R.id.talkback_control_btn:
                            mEZTalk.setVoiceTalkStatus(false);
                            mTalkRingView.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    /**
     * 注册当音量发生变化时接收的广播
     */
    private void registerVolumeReceiver() {
        mVolumeReceiver = new VolumeReceiver();
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
        EZLog.infoLog(TAG, "setOrientation");
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
        EZLog.infoLog(TAG, "onConfigurationChanged");
        setOrientation(newConfig.orientation);
    }

    protected void setSurfaceSize() {
        EZLog.infoLog(TAG, "setSurfaceSize");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        boolean isWideScrren = mOrientationDetector.isWideScrren();
        //竖屏
        if (!isWideScrren) {
            mEZUIPlayerView.setSurfaceSize(dm.widthPixels, 0);
            mOverlayTopBar.setVisibility(View.GONE);
            mTopBar.setVisibility(View.VISIBLE);
        } else {
            if (mIsOnTalk) {
                return;
            }
            //横屏
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
        if (mEZUIPlayerView.getSurfaceView().getHolder() != null && h != 0) {
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


