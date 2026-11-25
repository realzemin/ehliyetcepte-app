package com.ehliyetcepte.app;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import org.json.JSONObject;

public class MainApplication extends Application {
    public static final String ONESIGNAL_APP_ID = "2c0dfdb8-40cf-4d29-9d05-744de782bfd3";

    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Başlatma
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        // Bildirime tıklandığında linki doğrudan uygulama içinde aç
        OneSignal.getNotifications().addClickListener(event -> {
            String targetUrl = null;
            JSONObject data = event.getNotification().getAdditionalData();
            if (data != null && data.has("url")) {
                targetUrl = data.optString("url");
            }
            if (targetUrl == null || targetUrl.trim().isEmpty()) {
                targetUrl = event.getNotification().getLaunchURL();
            }
            if (targetUrl == null || targetUrl.trim().isEmpty()) {
                targetUrl = "http://127.0.0.1:5700";
            }
            
            MainActivity.TARGET_URL = targetUrl;

            if (MainActivity.instance != null) {
                MainActivity.instance.runOnUiThread(() -> {
                    MainActivity.instance.loadTargetUrl(MainActivity.TARGET_URL);
                });
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("target_url", targetUrl);
                intent.setData(Uri.parse(targetUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}
