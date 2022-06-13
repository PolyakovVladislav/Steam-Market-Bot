package com.example.steammarketbot.core.eventBus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

class EventBus {

    private final Context context;
    private final LocalBroadcastManager lbr;
    private BroadcastReceiveListener broadcastReceiveListener;

    final static String ACTIVITY_ACTION = "activity_action";
    final static String SERVICE_ACTION = "service_action";

    private String actionFor;

    private static final String DATA = "data";
    private static final String TASK = "task";
    private static final String TASK_USER_LOGIN = "task_user_login";
    private static final String TASK_USER_LOGGED_IN = "task_user_logged_in";
    private static final String TASK_TOAST = "task_toast";
    private static final String TASK_REFRESH_VILLAGES = "task_refresh_villages";
    private static final String TASK_CHECK_FOR_ATTACKS = "task_check_for_attacks";
    private static final String TASK_VILLAGES_REFRESHED = "task_villages_refreshed";
    private static final String TASK_GET_DORF1 = "task_get_dorf1";
    private static final String TASK_GET_DORF2 = "task_get_dorf2";
    private static final String TASK_GOT_DORF1 = "task_got_dorf1";
    private static final String TASK_GOT_DORF2 = "task_got_dorf2";
    private static final String TASK_BUILDING_TASK_LIST_CHANGED = "task_building_task_list_changed";
    private static final String TASK_QUIT = "task_quit";


    EventBus(Context context) {
        this.context = context;
        lbr = LocalBroadcastManager.getInstance(context);
    }

    void registerLBR(BroadcastReceiveListener broadcastReceiveListener, String intentFilter) {
        lbr.registerReceiver(broadcastReceiver, new IntentFilter(intentFilter));
        this.broadcastReceiveListener = broadcastReceiveListener;
        switch (intentFilter) {
            case ACTIVITY_ACTION:
                actionFor = SERVICE_ACTION;
                break;
            case SERVICE_ACTION:
                actionFor = ACTIVITY_ACTION;
                break;
        }
    }

    void unregisterLBR() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        broadcastReceiveListener = null;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadcastReceiveListener.onBroadcastReceived(intent);
        }
    };

    public interface BroadcastReceiveListener {
        void onBroadcastReceived(Intent intent);
    }

    void sendBroadcast(String task, String data) {
        Intent intent = new Intent(actionFor);
        intent.putExtra(TASK, task);
        intent.putExtra(DATA, data);
        sendLBR(this.context, intent);
    }

    void sendBroadcast(String task, String[] data) {
        Intent intent = new Intent(actionFor);
        intent.putExtra(TASK, task);
        intent.putExtra(DATA, data);
        sendLBR(this.context, intent);
    }

    void sendBroadcast(String task, boolean data) {
        Intent intent = new Intent(actionFor);
        intent.putExtra(TASK, task);
        intent.putExtra(DATA, data);
        sendLBR(this.context, intent);
    }

    void sendBroadcast(String task, int data) {
        Intent intent = new Intent(actionFor);
        intent.putExtra(TASK, task);
        intent.putExtra(DATA, data);
        sendLBR(this.context, intent);
    }

    void sendBroadcast(String task) {
        Intent intent = new Intent(actionFor);
        intent.putExtra(TASK, task);
        sendLBR(this.context, intent);
    }

    private void sendLBR (Context context, Intent intent) {
        lbr.sendBroadcast(intent);
    }
}
