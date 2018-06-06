package com.innotek.ifieldborad.utils;

import android.os.AsyncTask;
import android.os.Build;

import com.google.gson.Gson;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * Created by Raleigh.Luo on 18/6/6.
 */

public class RequestUtil {
    private RequestFinishListener listener;
    private RequestAsyn downloadAsyn;
    private String mUrl;


    public void setListener(RequestFinishListener listener) {
        this.listener = listener;
    }

    public void start(String url){
        this.mUrl=url;
        startAsynTask();
    }

    public void onStop(){
        this.listener=null;
        if (downloadAsyn != null) {
            if (downloadAsyn.getStatus() == AsyncTask.Status.RUNNING) downloadAsyn.cancel(true);
            downloadAsyn = null;
        }
    }
    /**
     * 鍚姩寮傛浠诲姟
     *
     */
    private void startAsynTask() {
        try {
            if (downloadAsyn != null) {
                if (downloadAsyn.getStatus() == AsyncTask.Status.RUNNING) downloadAsyn.cancel(true);
                downloadAsyn = null;
            }
            downloadAsyn = new RequestAsyn();
            downloadAsyn.setOnAsynFinishListener(onAsynFinishListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                downloadAsyn.executeOnExecutor(Executors.newCachedThreadPool());
            } else {
                downloadAsyn.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private OnAsynFinishListener onAsynFinishListener=new OnAsynFinishListener() {
        @Override
        public void onFinish(VersionEntity response) {
            try {
                int code=response.getCode();
                if(code== HttpStatus.SC_CREATED||code== HttpStatus.SC_OK)
                {
                    listener.onSuccess(response);
                }else if(code== HttpStatus.SC_REQUEST_TIMEOUT){
                    startAsynTask();
                }else{
                    listener.onFailed(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailed(null);
            }
        }


    };

    public interface RequestFinishListener {
        public void onSuccess(VersionEntity response);
        public void onFailed(String error);
    }
    class RequestAsyn extends AsyncTask<Void, Integer, VersionEntity> {

        private OnAsynFinishListener listener;
        public void setOnAsynFinishListener(OnAsynFinishListener listener){
            this.listener=listener;
        }
        @Override
        protected VersionEntity doInBackground(Void... params) {
            VersionEntity response=null;
            try {
                URL url = new URL(mUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();

                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode=connection.getResponseCode();
                InputStream inputStream=null;
                if (responseCode==200) {
                    inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line);

                    }
                    JSONObject json=JSONUtil.getJSONObject(buffer.toString());
                    response=new Gson().fromJson(buffer.toString(),VersionEntity.class);
                    response.setCode(responseCode);
                }
                connection.disconnect();
                inputStream.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(VersionEntity response) {
            try {
                if(listener!=null)listener.onFinish(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public interface OnAsynFinishListener{
        public void onFinish(VersionEntity response);
    };

}
