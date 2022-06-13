package com.example.steammarketbot.core.alarmManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

public class ConnectionInfo {

    private boolean connected;
    private boolean unMetered;
    private final ConnectionStateChangeListener mConnectionStateChangeListener;
    private final ConnectivityManager.NetworkCallback networkCallback;
    private final Context context;

    public ConnectionInfo(Context context, ConnectionStateChangeListener connectionStateChangeListener) {

        this.context = context;
        mConnectionStateChangeListener = connectionStateChangeListener;

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                unMetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
                connected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                mConnectionStateChangeListener.onConnectionStateChanged(connected, unMetered);
            }
        };

        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    public void unregisterNetworkCallback() {
        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isUnMetered() {
        return unMetered;
    }

    public interface ConnectionStateChangeListener {
        void onConnectionStateChanged(boolean isConnected, boolean isWifi);
    }
}