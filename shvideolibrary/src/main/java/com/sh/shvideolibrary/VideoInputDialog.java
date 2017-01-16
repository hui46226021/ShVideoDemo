package com.sh.shvideolibrary;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhush on 2016/11/11
 * E-mail zhush@jerei.com
 * PS  小视频输入控件
 */

public class VideoInputDialog extends DialogFragment {

    private static final String TAG = "VideoInputDialog";
    private Camera mCamera;
    private CameraPreview mPreview;
    private ProgressBar mProgressRight,mProgressLeft;
    private MediaRecorder mMediaRecorder;
    private Timer mTimer;
    private final int MAX_TIME = 1500;
    private int mTimeCount;
    private long time;
    private boolean isRecording = false;
    private String fileName;
    private VideoCall videoCall;

    public static int Q480 = CamcorderProfile.QUALITY_480P;
    public static int Q720 = CamcorderProfile.QUALITY_720P;
    public static int Q1080 = CamcorderProfile.QUALITY_1080P;
    public static int Q21600 = CamcorderProfile.QUALITY_2160P;
    private int quality =CamcorderProfile.QUALITY_480P;

    Context mContext;

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            mProgressRight.setProgress(mTimeCount);
            mProgressLeft.setProgress(mTimeCount);
        }
    };
    private Runnable sendVideo = new Runnable() {
        @Override
        public void run() {
            recordStop();
        }
    };

    public void setVideoCall(VideoCall videoCall) {
        this.videoCall = videoCall;
    }

    public static VideoInputDialog newInstance(VideoCall videoCall,int quality,Context context) {
        VideoInputDialog dialog = new VideoInputDialog();
        dialog.setVideoCall(videoCall);
        dialog.setQuality(quality);
        dialog.setmContext(context);
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.maskDialog);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_video_input, container, false);
        //打开相机
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getActivity(), mCamera);
        FrameLayout preview = (FrameLayout) v.findViewById(R.id.camera_preview);
        mProgressRight = (ProgressBar) v.findViewById(R.id.progress_right);
        mProgressLeft = (ProgressBar) v.findViewById(R.id.progress_left);
        mProgressRight.setMax(MAX_TIME);
        mProgressLeft.setMax(MAX_TIME);
        mProgressLeft.setRotation(180);
        ImageButton record = (ImageButton) v.findViewById(R.id.btn_record);
        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下 开始录像
                        if (!isRecording) {
                            if (prepareVideoRecorder()) {
                                time = Calendar.getInstance().getTimeInMillis(); //倒计时
                                mMediaRecorder.start();
                                isRecording = true;
                                mTimer = new Timer();
                                mTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        mTimeCount++;
                                        mainHandler.post(updateProgress);
                                        if (mTimeCount == MAX_TIME) {
                                            mainHandler.post(sendVideo);
                                        }
                                    }
                                    }, 0, 10);
                            } else {
                                releaseMediaRecorder();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起 停止录像
                        recordStop();
                        break;
                }
                return true;
            }
        });
        preview.addView(mPreview);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        recordStop();
        releaseMediaRecorder();
        releaseCamera();
    }

    /**
     * 停止录制
     */
    private void recordStop(){
        if (isRecording) {
            isRecording = false;
            if (isLongEnough()){
                mMediaRecorder.stop();
            }
            releaseMediaRecorder();
            mCamera.lock();
            if (mTimer != null) mTimer.cancel();
            mTimeCount = 0;
            mainHandler.post(updateProgress);

        }
    }


    /**
     *
     * @param ft
     * @param videoCall  录制视频回调
     * @param quality 分辨率
     * @param context
     */
    public static void show(FragmentManager ft,VideoCall videoCall,int quality,Context context){

        DialogFragment newFragment = VideoInputDialog.newInstance(videoCall,quality, context);
        newFragment.show(ft, "VideoInputDialog");
    }



    /** A safe way to get an instance of the Camera object. */
    private static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }



    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
            if (isLongEnough()){
                videoCall.videoPathCall(fileName);
            }else{
                Toast.makeText(getContext(), getString(R.string.chat_video_too_short), Toast.LENGTH_SHORT).show();
            }
            dismiss();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    //初始化 mMediaRecorder 用于录像
    private boolean prepareVideoRecorder(){

        if (mCamera==null) return false;
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        //声音
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        //视频
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置分辨率为480P
        mMediaRecorder.setProfile(CamcorderProfile.get(quality));
        //路径
        mMediaRecorder.setOutputFile(getOutputMediaFile().toString());
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());


        try {
            //旋转90度 保持竖屏
            mMediaRecorder.setOrientationHint(90);
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }



    /** Create a File for saving an image or video */
    private File getOutputMediaFile(){

//        return  new File(getContext().getExternalCacheDir().getAbsolutePath() + "/" + fileName);
//        PackageManager pm = mContext.getPackageManager();
        String appName =      mContext. getPackageName();
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + appName);
        if (!dir.exists()){
            dir.mkdir();
        }
         fileName = dir+ "/video_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        Log.i("filePath",fileName);
        return  new File(fileName);
    }

    /**
     * 判断录制时间
     * @return
     */
    private boolean isLongEnough(){
        return Calendar.getInstance().getTimeInMillis() - time > 3000;
    }

    /**
     * Created by zhush on 2016/11/11
     * E-mail zhush@jerei.com
     * PS  录制视频回调
     */

    public static interface VideoCall{
        public void videoPathCall(String path);
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
