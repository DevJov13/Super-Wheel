package gme.superwheel.jov;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainContent extends AppCompatActivity {



    private boolean hasUserConsent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        WebView appcontent = findViewById(R.id.web_view);
        appcontent.setWebViewClient(new WebViewClient());
        appcontent.getSettings().setJavaScriptEnabled(true);
        appcontent.loadUrl(ConfigurationGlobal.gameURL);




    }
}