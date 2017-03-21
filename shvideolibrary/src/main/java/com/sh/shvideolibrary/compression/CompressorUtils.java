package com.sh.shvideolibrary.compression;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import java.io.File;

/**
 * Created by zhush on 2017/1/14.
 * E-mail zhush@jerei.com
 * PS
 */
public class CompressorUtils {

    //视频 输入地址
    private String currentInputVideoPath = "";
    //视频数据地址
    private String currentOutputVideoPath="";

    static String TAG="CompressorUtils";

    Compressor  mCompressor;
    Activity activity;
    public CompressorUtils(String currentInputVideoPath, String currentOutputVideoPath,final Activity activity) {
        this.currentInputVideoPath = currentInputVideoPath;
        this.currentOutputVideoPath = currentOutputVideoPath;
        this.activity = activity;
        mCompressor  = new Compressor(activity);
        mCompressor.loadBinary(new InitListener() {
            @Override
            public void onLoadSuccess() {
                Log.e(TAG, "load library succeed");

            }
            @Override
            public void onLoadFail(String reason) {
                Log.e(TAG, "load library fail:" + reason);
            }
        });
    }


    /**
     * 压缩
     */
    public void execCommand(CompressListener compressListener) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            compressListener.onExecFail("暂时不支持android7.0以上版本");
            return;
        }

        String  command ="-y -i " + currentInputVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 640x480 -aspect 16:9 " + currentOutputVideoPath;;

        File mFile = new File(currentOutputVideoPath);
        if (mFile.exists()) {
            mFile.delete();
        }
        mCompressor.execCommand(command,compressListener);
    }
}
