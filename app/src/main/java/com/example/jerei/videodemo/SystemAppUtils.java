package com.example.jerei.videodemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by zhush on 2016/10/24.
 * E-mail zhush@jerei.com
 */

public class SystemAppUtils {
    //文件类型后缀
    private static final String[][] MIME_MapTable = { { ".3gp", "video/3gpp" }, { ".apk", "application/vnd.android.package-archive" }, { ".asf", "video/x-ms-asf" }, { ".avi", "video/x-msvideo" }, { ".bin", "application/octet-stream" }, { ".bmp", "image/bmp" }, { ".c", "text/plain" }, { ".class", "application/octet-stream" }, { ".conf", "text/plain" }, { ".cpp", "text/plain" }, { ".doc", "application/msword" }, { ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" }, { ".xls", "application/vnd.ms-excel" }, { ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" }, { ".exe", "application/octet-stream" }, { ".gif", "image/gif" }, { ".gtar", "application/x-gtar" }, { ".gz", "application/x-gzip" }, { ".h", "text/plain" }, { ".htm", "text/html" }, { ".html", "text/html" }, { ".jar", "application/java-archive" }, { ".java", "text/plain" }, { ".jpeg", "image/jpeg" }, { ".jpg", "image/jpeg" }, { ".js", "application/x-javascript" }, { ".log", "text/plain" }, { ".m3u", "audio/x-mpegurl" }, { ".m4a", "audio/mp4a-latm" }, { ".m4b", "audio/mp4a-latm" }, { ".m4p", "audio/mp4a-latm" }, { ".m4u", "video/vnd.mpegurl" }, { ".m4v", "video/x-m4v" }, { ".mov", "video/quicktime" }, { ".mp2", "audio/x-mpeg" }, { ".mp3", "audio/x-mpeg" }, { ".mp4", "video/mp4" }, { ".mpc", "application/vnd.mpohun.certificate" }, { ".mpe", "video/mpeg" }, { ".mpeg", "video/mpeg" }, { ".mpg", "video/mpeg" }, { ".mpg4", "video/mp4" }, { ".mpga", "audio/mpeg" }, { ".msg", "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" }, { ".pdf", "application/pdf" }, { ".png", "image/png" }, { ".pps", "application/vnd.ms-powerpoint" }, { ".ppt", "application/vnd.ms-powerpoint" }, { ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" }, { ".prop", "text/plain" }, { ".rc", "text/plain" }, { ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" }, { ".sh", "text/plain" }, { ".tar", "application/x-tar" }, { ".tgz", "application/x-compressed" }, { ".txt", "text/plain" }, { ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" }, { ".wmv", "audio/x-ms-wmv" }, { ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" }, { ".z", "application/x-compress" }, { ".zip", "application/x-zip-compressed" }, { "", "*/*" } };


    /**
     * 打开文件
     * @param file
     */
    public static void openFile(File file, Activity activity){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, type);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    /**
     * 获取文件类型后缀
     * @param paramFile
     * @return
     */
    private static String getMIMEType(File paramFile)
    {
        String str1 = "*/*";
        String str2 = paramFile.getName();
        int i = str2.lastIndexOf(".");
        if (i < 0)
            return str1;
        String str3 = str2.substring(i, str2.length()).toLowerCase();
        if (str3 == "")
            return str1;
        for (int j = 0; ; j++)
        {
            if (j >= MIME_MapTable.length)
                return str1;
            if (str3.equals(MIME_MapTable[j][0]))
                str1 = MIME_MapTable[j][1];
        }
    }
}
