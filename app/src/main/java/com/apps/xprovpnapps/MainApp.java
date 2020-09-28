package com.apps.xprovpnapps;


import android.app.Application;
import android.content.ContextWrapper;
import android.util.Log;

import com.anchorfree.hydrasdk.HydraSDKConfig;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.ClientInfo;
import com.anchorfree.hydrasdk.vpnservice.connectivity.NotificationConfig;
import com.apps.xprovpnapps.xpro.R;
import com.onesignal.OneSignal;
import com.pixplicity.easyprefs.library.Prefs;

public class MainApp extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        //Prefs lib
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        // OneSignal Initialization

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        hydraInit();
    }

    public void hydraInit() {
        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl("https://backend.northghost.com")
                .carrierId("382715_382715")

                .build();

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .build();

        HydraSdk.setLoggingLevel(Log.VERBOSE);

        HydraSDKConfig config = HydraSDKConfig.newBuilder()
                .observeNetworkChanges(true)
                .captivePortal(true)
                .moveToIdleOnPause(false)
                .build();
        HydraSdk.init(this, clientInfo, notificationConfig, config);
    }



}

