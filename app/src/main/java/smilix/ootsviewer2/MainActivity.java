package smilix.ootsviewer2;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.toString();

    private static final String LAST_URL = "lastUrl";
    private static final String PREFS_NAME = "settings";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // https://developer.chrome.com/multidevice/webview/gettingstarted

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new RestrictedWebViewClient(new RestrictedWebViewClient.OnNewUrlCallback() {
            @Override
            public void onNewUrl(String url) {
                saveUrl(url);
            }
        }, "www.giantitp.com"));


        String lastUrl = loadUrl();
        mWebView.loadUrl(lastUrl);
    }

    private void saveUrl(String url) {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LAST_URL, url);
        editor.apply();
    }

    private String loadUrl() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        String url = settings.getString(LAST_URL, "http://www.giantitp.com/comics/oots.html");
        Log.d(TAG, "loaded strip settings: " + url);
        return url;
    }
}
