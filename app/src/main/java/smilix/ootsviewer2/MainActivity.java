package smilix.ootsviewer2;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends FullscreenFragmentActivity {
    private static final String TAG = MainActivity.class.toString();

    private static final String CURRENT_STRIP = "currentStrip";
    private static final String PREFS_NAME = "settings";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // https://developer.chrome.com/multidevice/webview/gettingstarted

        WebView mWebView = (WebView) findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return MainActivity.this.onTouchEvent(event);
            }
        });

        int number = loadStripNumber();

        new AppCtrl(mWebView, number, new AppCtrl.AppCtrlCallbacks() {
            @Override
            public void onNewNumber(int number) {
                saveStripNumber(number);
            }
        });
    }

    private void saveStripNumber(int number) {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(CURRENT_STRIP, number);
        editor.apply();
    }

    private int loadStripNumber() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(CURRENT_STRIP, 1);
    }
}
