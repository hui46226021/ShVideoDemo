package com.example.jerei.videodemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhush on 2016/11/11
 * E-mail zhush@jerei.com
 * PS  視頻DEMO
 */
public class MainActivity extends AppCompatActivity implements VideoInputDialog.VideoCall{

    ImageView image;
    Button button;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        //显示视频录制控件
                        VideoInputDialog.show(getSupportFragmentManager(),MainActivity.this);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView();
            }
        });
    }


    @Override
    public void videoPathCall(String path) {

        Log.e("地址:",path);
        //根据视频地址获取缩略图
        this.path =path;
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        image.setImageBitmap(bitmap);
    }

    public void openView(){
        if(TextUtils.isEmpty(path)){

            return;
        }
        File file = new File(path);
        SystemAppUtils.openFile(file,this);
    }
}
