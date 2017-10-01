package com.msx.linker;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/7/1.
 */

public class PathChecker {
   public boolean check(String path){
       ImgThread img = new ImgThread();
       img.path = path;
       img.start();
       Log.d("arr","check begin");
       while(!img.isOver){}
       Log.d("arr","check over;" + !img.isError + "path:" + path);
       return !img.isError;
   }

    class ImgThread extends Thread
    {
        String path;
        boolean isOver;
        boolean isError;
        @Override
        public void run()
        {
            try
            {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(2000);
//                conn.setRequestMethod("GET");
                Log.d("arr","http begin " + path);
                InputStream inputStream = conn.getInputStream();
                inputStream.close();
                isError = false;

//                if (conn.getResponseCode() == 200){
//                    isError = false;
//                    Log.d("arr","success " + path);
//                }else{
//                    isError = true;
//                    Log.d("arr","fail " + path);
//                }
                isOver = true;
                Log.d("arr","http over");
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                isError = true;
                isOver = true;
                Log.d("arr","fail " + path);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                isError = true;
                isOver = true;
                Log.d("arr","fail " + path);
            }
        }
    }
}
