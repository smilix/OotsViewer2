package smilix.ootsviewer2;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by holger on 05.06.16.
 */
public class RestrictedWebViewClient extends WebViewClient {

    private final OnNewUrlCallback onUrlLoaded;
    private final String[] hostList;

    public RestrictedWebViewClient(OnNewUrlCallback onUrlLoaded, String... hosts) {
        this.onUrlLoaded = onUrlLoaded;
        this.hostList = hosts;
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        for (String host : hostList) {
            if (Uri.parse(url).getHost().endsWith(host)) {
                return false;
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
        return true;
    }

    String convertStreamToString(java.io.InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

//    @Override
//    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//        System.out.println("shouldInterceptRequest2");
//
//        return super.shouldInterceptRequest(view, url);
//    }
//
//    @Override
//    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//        System.out.println("shouldInterceptRequest");
//        if (!request.getUrl().getPath().endsWith(".html")) {
//            System.out.println("requesting " + request.getUrl());
//            return null;
//        }
//
//        final WebResourceResponse webResourceResponse = super.shouldInterceptRequest(view, request);
//        System.out.println("data: " + convertStreamToString(webResourceResponse.getData()));
//
//        return webResourceResponse;
//    }

    //    @Override
//    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//        super.onPageStarted(view, url, favicon);
//        System.out.println("started " + url);
//    }
//
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        String cssCode = "body > table > tbody > tr:first-child, body > table > tbody > tr > td > table > tbody > tr > td:first-child { display: none; }";
        String js = "var s = document.createElement('style');";
        js += "s.innerHTML = '" + cssCode + "';";
        js += "document.getElementsByTagName('head')[0].appendChild(s);";

        view.loadUrl("javascript:" + js);
        onUrlLoaded.onNewUrl(url);
    }

    public interface OnNewUrlCallback {
        void onNewUrl(String url);
    }
}