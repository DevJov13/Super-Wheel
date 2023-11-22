package gme.superwheel.jov;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConfigurationGlobal extends Application {

    private static boolean hasUserConsent = false;
    public static String urlAPI = "";
    public static String gameURL = "";
    public static String policyURL = "";
    public static String success = "";

    private static final String APP_CODE = "TG12105";
    private static final String USER_CONSENT = "userConsent";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(APP_CODE, Context.MODE_PRIVATE);
        hasUserConsent = prefs.getBoolean(USER_CONSENT, false);

        // Initialize Facebook SDK, Adjust, AppsFlyer
        // TODO: Initialize the necessary SDKs here.
    }

    //region [ Firebase Remote Config ]
    public void setupRemoteConfig(Context context, Activity activity, Boolean hasFirebase, Boolean hasPolicy) {
        if (Boolean.TRUE.equals(hasFirebase)) {
            FirebaseApp.initializeApp(context);
            FirebaseRemoteConfig remoteCFG = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings settingsCFG = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build();

            remoteCFG.setConfigSettingsAsync(settingsCFG);

            remoteCFG.fetchAndActivate().addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    Log.d("FirebaseCFG:", "Loading Successful");
                    urlAPI = remoteCFG.getString("urlAPI");

                    String endPoint = urlAPI + "?request&appid=" + APP_CODE;
                    Log.d("WZ", urlAPI);
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, endPoint,
                            response -> {
                                Gson parseValue = new Gson();
                                JsonObject jsonObject = parseValue.fromJson(response, JsonObject.class);
                                gameURL = jsonObject.get("gameURL").getAsString();
                                success = jsonObject.get("status").getAsString();
                                policyURL = jsonObject.get("policyURL").getAsString();


                                Log.d("gameUrl:", gameURL);
                                Log.d("policyURL:", policyURL);

                                if (!hasUserConsent && Boolean.TRUE.equals(hasPolicy)) {
                                    this.showConsentDialog(context, activity);
                                }
                                else
                                    loadActivity(activity);
                            },
                            error -> {

                                // Handle the error here
                                Log.e("VolleyError", "Error: " + error.getMessage());
                            }
                    );
                    requestQueue.add(stringRequest);
                } else {
                    Log.e("FirebaseCFG:", "Loading not Successful", task.getException());
                }
            });
        }
    }
    //endregion [ Firebase Remote Config ]

    //region [ User Consent / Data Policy ]
    public void checkUserConsent(Context context, Activity activity, Boolean hasPolicy) {
        setupRemoteConfig(context, activity, true, hasPolicy);

    }

    private void showConsentDialog(Context context, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_consent, null);
        WebView userConsent = dialogView.findViewById(R.id.userConsent);

        userConsent.setWebViewClient(new WebViewClient());
        userConsent.loadUrl("https://5gbapps.site/policy");

        builder.setTitle("Data Privacy Policy");
        builder.setView(dialogView);

        builder.setPositiveButton("I Agree", (dialog, which) -> {
            setConsentValue(true);
            loadActivity(activity);
        });
        builder.setNegativeButton("Don't Agree", (dialog, which) -> {
            activity.finishAffinity();
        });

        builder.show();
    }

    private void setConsentValue(boolean userChoice) {
        hasUserConsent = userChoice;

        // Initialize Facebook, Adjust, AppsFlyer as needed
        // TODO: Set up the SDKs based on user consent here.

        SharedPreferences prefs = getSharedPreferences(APP_CODE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(USER_CONSENT, userChoice);
        editor.apply();
    }

    public Boolean getUserConsent() {
        SharedPreferences prefs = getSharedPreferences(APP_CODE, Context.MODE_PRIVATE);
        hasUserConsent = prefs.getBoolean(USER_CONSENT, false);
        return hasUserConsent;
    }

    private void loadActivity(Activity activity) {
        Intent newActivity = new Intent(activity, MainContent.class);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(newActivity);
    }
    //endregion [ User Consent / Data Policy ]
}
