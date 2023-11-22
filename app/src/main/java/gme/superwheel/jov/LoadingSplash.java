package gme.superwheel.jov;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingSplash extends AppCompatActivity {

    private boolean hasUserConsent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading_splash);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        ((ConfigurationGlobal) getApplication()).checkUserConsent(this, this, true);

        VideoView splash = findViewById(R.id.videoView);
        Uri splashFile = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.loadz);
        splash.setVideoURI(splashFile);

        splash.start();
        splash.setOnCompletionListener(mediaPlayer -> {
            splash.stopPlayback();
            splash.setVisibility(View.GONE);

            hasUserConsent = ((ConfigurationGlobal) getApplication()).getUserConsent();

            if (hasUserConsent) {
                new Handler().postDelayed(() -> {
                    Intent appContent = new Intent(LoadingSplash.this, MainContent.class);
                    appContent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(appContent);
                    finish();
                }, 6000);
            }
        });
    }
}
