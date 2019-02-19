package com.kobyakov.githubrepos;

import android.app.Application;
import android.os.Build;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class App extends Application {

    public static App INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;

        fixSSLExceptionOnAndroid4();
    }

    private void fixSSLExceptionOnAndroid4() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                ProviderInstaller.installIfNeeded(getApplicationContext());
                SSLContext sslContext;
                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                sslContext.createSSLEngine();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                    | NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }
    }
}