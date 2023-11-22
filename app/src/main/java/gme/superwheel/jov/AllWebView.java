package gme.superwheel.jov;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AllWebView extends WebView {



    private class JSInterface
    {
        @JavascriptInterface
        public void postMessage(String name, String data)
        {
            Map<String, Object> eventValue = new HashMap<>();

            if ("openWindow".equals(name)) {

                try {
                    JSONObject extLink = new JSONObject(data);

                    Intent newWindow = new Intent(Intent.ACTION_VIEW);
                    newWindow.setData(Uri.parse(extLink.getString("url")));
                    newWindow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(newWindow);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            } else {
                eventValue.put(name, data);
            }
        }
    }

    public AllWebView(Context context) {
        super(context);
        initWebViewSettings();
    }

    public AllWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebViewSettings();
    }

    public AllWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWebViewSettings();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        setWebViewClient(new CustomWebClient());

    }

    private class CustomWebClient extends WebViewClient {
        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            new Handler().postDelayed(() -> view.evaluateJavascript(

                    "(function() { " +
                            "   if(document.getElementById('pngPreloaderWrapper')) {" +
                            "       document.getElementById('pngPreloaderWrapper').removeChild(document.getElementById('pngLogoWrapper')); " +
                            "   }" +
                            "})();",
                    html -> {
                    }), 600);







            new Handler().postDelayed(() -> view.evaluateJavascript(
                    "(function() { " +
                            "   var myHome = document.getElementById('lobbyButtonWrapper'); " +
                            "   if(document.getElementById('lobbyButtonWrapper')) {" +
                            "       document.getElementById('lobbyButtonWrapper').style = 'display:none;';" +
                            "   }" +
                            "});",
                    html -> {
                    }), 5000);

            // Define the JavaScript code to remove the element
            String removeElementCode = "(function() { " +
                    "   var elementToRemove = document.querySelector('.suggest-download-h5_top');" +
                    "   if (elementToRemove) {" +
                    "       elementToRemove.parentNode.removeChild(elementToRemove);" +
                    "   }" +
                    "})();";

            // Execute the JavaScript code to remove the specified element
            view.evaluateJavascript(removeElementCode, null);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            new Handler().postDelayed(() -> view.evaluateJavascript(
                    "(function() { document.getElementById('suggest-download-h5_top').innerHTML = ''; document.getElementById('headerWrap').style = 'position:fixed; top:0px;';})();",
                    html -> {
                    }), 1800);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            new Handler().postDelayed(() -> view.evaluateJavascript(
                    "(function() { document.getElementById('suggest-download-h5_top').innerHTML = ''; document.getElementById('d-header').style = 'position:fixed; top:0px;';})();",
                    html -> { }), 5400);
        }
    }
}