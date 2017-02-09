package com.example.jerei.videodemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.sh.shvideolibrary.VideoInputActivity;
import com.sh.shvideolibrary.VideoInputDialog;
import com.sh.shvideolibrary.compression.CompressListener;
import com.sh.shvideolibrary.compression.CompressorUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhush on 2016/11/11
 * E-mail zhush@jerei.com
 * PS  視頻DEMO
 */
public class MainActivity extends AppCompatActivity implements VideoInputDialog.VideoCall{

    ImageView image;
    ImageView imag2;
    Button button;
    Button button2;
    Button button3;

    TextView first;
    TextView back;
    ProgressBar  progressBar;


    static String TAG="MainActivity";

    String path;//视频录制输出地址
    //视频压缩数据地址
    private String currentOutputVideoPath = "/mnt/sdcard/out.mp4";
    private static final int REQUEST_CODE_FOR_RECORD_VIDEO = 5230;//录制视频请求码
    Double videoLength=0.0;//视频时长
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        first = (TextView) findViewById(R.id.first);
        back = (TextView) findViewById(R.id.back);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        imag2 = (ImageView) findViewById(R.id.imag2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        //显示视频录制控件
                        VideoInputDialog.show(getSupportFragmentManager(),MainActivity.this,VideoInputDialog.Q720,MainActivity.this);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoInputActivity.startActivityForResult(MainActivity.this, REQUEST_CODE_FOR_RECORD_VIDEO,VideoInputActivity.Q720);
            }
        });
        /**
         * 压缩视频
         */
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取视频时长  计算压缩进度用
                MediaMetadataRetriever retr = new MediaMetadataRetriever();
                retr.setDataSource(path);
                String time = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//获取视频时长
                //7680
                try {
                    videoLength = Double.parseDouble(time)/1000.00;
                } catch (Exception e) {
                    e.printStackTrace();
                    videoLength = 0.00;
                }
                Log.v(TAG, "videoLength = "+videoLength + "s");


                /**
                 * 压缩视频
                 */
                CompressorUtils compressorUtils = new CompressorUtils(path,currentOutputVideoPath,MainActivity.this);
                compressorUtils.execCommand(new CompressListener() {
                    @Override
                    public void onExecSuccess(String message) {
                        Log.i(TAG, "success " + message);
                        progressBar.setVisibility(View.INVISIBLE);
                        textAppend(getString(R.string.compress_succeed));
                        back.setText(getFileSize(currentOutputVideoPath));
                        //获取缩略图
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(currentOutputVideoPath, MediaStore.Video.Thumbnails.MINI_KIND);
                        imag2.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onExecFail(String reason) {
                        Log.i(TAG, "fail " + reason);
                    }

                    @Override
                    public void onExecProgress(String message) {
                        progressBar.setVisibility(View.VISIBLE);
                        textAppend(getString(R.string.compress_progress, message));

                        int i = getProgress(message);
                        Log.e("进度",i+"");
                        progressBar.setProgress(i);
                    }
                });
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(path);
            }
        });
        imag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(currentOutputVideoPath);
            }
        });




    }

    /**
     * 小视屏录制回调
     * @param path
     */
    @Override
    public void videoPathCall(String path) {

        Log.e("地址:",path);
        //根据视频地址获取缩略图
        this.path =path;
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        image.setImageBitmap(bitmap);
        first.setText(getFileSize(path));


    }

    /**
     * 录制视频回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_FOR_RECORD_VIDEO&&resultCode==RESULT_CANCELED){

        }
        if(requestCode==REQUEST_CODE_FOR_RECORD_VIDEO&&resultCode==RESULT_OK){
            String path = data.getStringExtra(VideoInputActivity.INTENT_EXTRA_VIDEO_PATH);
            Log.e("地址:",path);
            //根据视频地址获取缩略图
            this.path =path;
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
            image.setImageBitmap(bitmap);
            first.setText(getFileSize(path));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void openView(String path){
        if(TextUtils.isEmpty(path)){

            return;
        }
        File file = new File(path);
        SystemAppUtils.openFile(file,this);
    }



    private String getFileSize(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return "0 MB";
        } else {
            long size = f.length();
            return (size / 1024f) / 1024f + "MB";
        }
    }
    int progress=0;
    private int getProgress(String source){
        // Duration: 00:00:22.50, start: 0.000000, bitrate: 13995 kb/s

        //progress frame=   28 fps=0.0 q=24.0 size= 107kB time=00:00:00.91 bitrate= 956.4kbits/s
        if(source.contains("start: 0.000000")){
            return progress;
        }
        Pattern p = Pattern.compile("00:\\d{2}:\\d{2}");
        Matcher m = p.matcher(source);
        if (m.find()) {
            //00:00:00
            String result = m.group(0);
            String temp[] = result.split(":");
            Double seconds = Double.parseDouble(temp[1]) * 60 + Double.parseDouble(temp[2]);

            if (0 != videoLength) {
                Log.v("进度长度", "current second = " + seconds+"/videoLength="+videoLength);
                progress = (int)(seconds *100/ videoLength);

                return progress;
            }
            return progress;
        }
        return progress;
    }


    private void textAppend(String text) {
        if (!TextUtils.isEmpty(text)) {
          Log.e("日志",text);
        }
    }
}
