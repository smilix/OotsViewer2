package smilix.ootsviewer2;

import android.os.AsyncTask;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by holger on 08.06.16.
 */
public class AppCtrl implements OotsUrlResolver.UrlResult {

    interface AppCtrlCallbacks {
        void onNewNumber(int number);
    }

    private final WebView mWebView;
    private int number;
    private AppCtrlCallbacks callbacks;
    private OotsUrlResolver ootsUrlResolver;


    public AppCtrl(WebView mWebView, int number, AppCtrlCallbacks callbacks) {
        this.mWebView = mWebView;
        this.number = number;
        this.callbacks = callbacks;

        this.ootsUrlResolver = new OotsUrlResolver(this);
        this.mWebView.addJavascriptInterface(this, "AppCtrl");
        this.mWebView.loadUrl("file:///android_asset/web/index.html");
    }

    @JavascriptInterface
    public void domReady() {
        loadStrip();
    }

    @JavascriptInterface
    public void next() {
        this.number++;
        loadStrip();
    }

    @JavascriptInterface
    public void prev() {
        this.number = Math.max(this.number - 1, 1);
        loadStrip();
    }

    @JavascriptInterface
    public void loadPage(int pageNumber) {
        this.number = Math.max(pageNumber, 1);
        loadStrip();
    }


    @Override
    public void onOotsUrlResolved(String imageUrl) {
        mWebView.evaluateJavascript("showImage('" + imageUrl + "')", null);
    }

    @Override
    public void onOotsUrlError(String reason) {
        reason = reason.replace("'", "\\'");
        mWebView.evaluateJavascript("showError('" + reason + "')", null);
    }


    private void loadStrip() {
        final int currentNumber = this.number;

        mWebView.post(new Runnable() {
            public void run() {
                mWebView.evaluateJavascript("updatePageNumber(" + currentNumber + ")", null);
                callbacks.onNewNumber(currentNumber);
                ootsUrlResolver.getImageUrl(currentNumber);
            }
        });

    }
}
