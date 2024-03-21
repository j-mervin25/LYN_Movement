package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class OfflineWebview extends AppCompatActivity {
    WebView mWebView;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_webview);
        String url = getIntent().getStringExtra("url");
        mWebView = findViewById(R.id.webview);
        mWebView.setWebViewClient(new myWebViewclient()); // to handle URL redirects in the app
        mWebView.getSettings().setJavaScriptEnabled(true); // to enable JavaScript on web pages
        mWebView.getSettings().setGeolocationEnabled(true); // to enable GPS location on web pages
        mWebView.loadUrl(url);

        b1 = findViewById(R.id.button1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OfflineWebview.this, ExamUpload.class);
                startActivity(i);
            }
        });

    }
    public class myWebViewclient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            final String urls = url;
            if (urls.contains("mailto") || urls.contains("whatsapp") || urls.contains("tel") || urls.contains("sms") || urls.contains("facebook") || urls.contains("truecaller") || urls.contains("twiter")) {
                mWebView.stopLoading();
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(urls));
                startActivity(i);

            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    }
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}