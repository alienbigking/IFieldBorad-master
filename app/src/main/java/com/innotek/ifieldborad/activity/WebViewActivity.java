package com.innotek.ifieldborad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.Expose;
import com.innotek.ifieldborad.R;
import com.innotek.ifieldborad.utils.DownloadApk;
import com.innotek.ifieldborad.utils.RequestUtil;
import com.innotek.ifieldborad.utils.VersionEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Raleigh.Luo on 18/6/6.
 */

public class WebViewActivity extends Activity {
    private ProgressBar mProgressBar;
    private EditText etUrl;
    private View mShowPanel;
    private TextView tvVersion,tvUpdateContent,tvUpdate,tvDownloadProgress,tvProgerssHint,tvCurrentVersion;
    private RequestUtil mRequest;
    private VersionEntity entity=null;
    private DownloadApk mDownloadApk;
    private int currentCode=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.webview_layout);
        mRequest=new RequestUtil();
        mRequest.setListener(mRequestFinishListener);
        mDownloadApk=new DownloadApk();
        mDownloadApk.setListener(mDownloadFinishListener);

        mShowPanel=findViewById(R.id.webview_layout_ll_show_root);

        mProgressBar=(ProgressBar)findViewById(R.id.webview_layout_progressbar);
        etUrl= (EditText) findViewById(R.id.webview_layout_et_url);
        tvProgerssHint= (TextView) findViewById(R.id.webview_layout_progressbar_hint);
        tvVersion= (TextView) findViewById(R.id.webview_layout_tv_laster_version);
        tvUpdateContent= (TextView) findViewById(R.id.webview_layout_tv_update_content);
        tvUpdate= (TextView) findViewById(R.id.webview_layout_tv_update);
        tvDownloadProgress= (TextView) findViewById(R.id.webview_layout_tv_update_progress);
        tvCurrentVersion= (TextView) findViewById(R.id.webview_layout_tv_current_version);

        String versionName="";

        try {
            PackageInfo p=getPackageManager().
                    getPackageInfo(getPackageName(), 0);
            currentCode=p.versionCode;
            versionName = getString(R.string.current_version) + p.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvCurrentVersion.setText(versionName);
        //Fir.im在线更新下载
        String api_id="58ef1718959d6945af00014f";
        String api_token="6cd60f899e9566df0f053cfe58a065b5";
        String urlStr=String.format("http://api.fir.im/apps/latest/%s?api_token=%s",api_id,api_token);
        start(urlStr);
    }
    private void start(String url){
        tvDownloadProgress.setVisibility(View.GONE);
        tvUpdate.setText(R.string.download_update);
        tvUpdate.setEnabled(false);
        tvUpdate.setBackgroundResource(R.drawable.grey_btn_shape);
        mShowPanel.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        tvProgerssHint.setVisibility(View.VISIBLE);
        tvProgerssHint.setText(R.string.requesting);
        mRequest.start(url);
    }
    public void onClick(View view){
        clapseSoftInputMethod();
        if(view.getId()== R.id.webview_layout_tv_back)finish();
        else if(view.getId()==R.id.webview_layout_tv_confirm){
            String url=etUrl.getText().toString().trim();
            if(url.length()>0){
                mDownloadApk.onStop();
                start(url);
            }
        }
        else if(view.getId()==R.id.webview_layout_tv_update){//下载更新
            tvUpdate.setText(R.string.downloading);
            tvUpdate.setEnabled(false);
            tvDownloadProgress.setVisibility(View.VISIBLE);
            tvDownloadProgress.setText("");
            mDownloadApk.download(entity.getInstall_url());
        }else if(view.getId()==R.id.webview_layout_tv_unintall){//卸载应用
            //调用卸载方法系统会弹出卸载APP对话框，点击确定就会立即卸载，不需要额外权限
            Uri uri = Uri.fromParts("package", "com.innotek.ifieldborad", null);
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            startActivity(intent);
        }
    }
    /**关闭虚拟键盘
     */
    void clapseSoftInputMethod() {

        try {//activity
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive())
                //键盘是打开的状态
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch ( Exception e) {
        }

    }
    private RequestUtil.RequestFinishListener mRequestFinishListener=new RequestUtil.RequestFinishListener() {
        @Override
        public void onSuccess(VersionEntity response) {
            try {
                entity = response;
                mShowPanel.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                tvProgerssHint.setVisibility(View.GONE);
                tvVersion.setText("V" + response.getVersionShort());
                if (currentCode < response.getVersion()) {
                    tvUpdate.setEnabled(true);
                    tvUpdate.setBackgroundResource(R.drawable.blue_btn_selector);
                }
                tvUpdateContent.setText(response.getChangelog().replace("\\n", "\n"));
            }catch (Exception e){}

        }

        @Override
        public void onFailed(String error) {
            mProgressBar.setVisibility(View.GONE);
            tvProgerssHint.setText(R.string.request_failed);
        }
    };
    private DownloadApk.DownloadFinishListener mDownloadFinishListener=new DownloadApk.DownloadFinishListener() {
        @Override
        public void onSuccess(String filePath) {
            tvDownloadProgress.setVisibility(View.GONE);
            tvUpdate.setText(R.string.download_update);
            tvUpdate.setEnabled(true);
            //安装apk
            File apkfile = new File(filePath);
            if (!apkfile.exists()) {
                return;
            }
            // 通过Intent安装APK文件
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                    "application/vnd.android.package-archive");
            startActivity(i);
        }

        @Override
        public void onFailed(String error) {
            tvDownloadProgress.setText(R.string.download_failed);
            tvUpdate.setText(R.string.download_update);
            tvUpdate.setEnabled(true);
        }

        @Override
        public void onProgress(int currentSize, int totalSize) {
            float total = Float.valueOf(totalSize) / 1024f / 1024f;
            float current = Float.valueOf(currentSize) / 1024f / 1024f;
            tvDownloadProgress.setText( String.format("%.2fMB/%.2fMB", current > total?total : current, total));
        }
    };


}
