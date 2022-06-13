package com.example.steammarketbot.core.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.example.steammarketbot.MainActivity;
import com.example.steammarketbot.R;

public class Notifications extends BroadcastReceiver {

    private final Context context;

    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_RESUME = "action_resume";
    public static final String ACTION_RESUME_AUTO_PAUSE = "action_resume_auto_pause";

    public static final int FOREGROUND_SERVICE_NOTIFICATION_ID = "foreground_service_notification_id_steam_market_bot".hashCode();
    public static final int INSTRUCTION_TIMEOUT_NOTIFICATION_ID = "instruction_timeout_notification_id".hashCode();
    public static final int INSTRUCTION_CONNECTION_FAIL_NOTIFICATION_ID = "instruction_connection_fail_notification_id".hashCode();

    public static final String NOTIFICATION_CHANNEL_FOREGROUND_SERVICE_ID = "Notification_channel_foreground_service_id";
    public static final String NOTIFICATION_CHANNEL_BASIC_ID = "Notification_channel_basic_id";
    public final String FOREGROUND_SERVICE_CHANNEL_NAME;
    public final String BASIC_CHANNEL_NAME;
    public final String FOREGROUND_SERVICE_CHANNEL_DESCRIPTION;
    public final String BASIC_CHANNEL_NAME_DESCRIPTION;

    private final NotificationActionButtonListener notificationActionButtonListener;

    public Notifications(Context context,
                         NotificationActionButtonListener notificationActionButtonListener,
                         String foregroundServiceChannelName,
                         String basicChannelName,
                         String foregroundServiceChannelDescription,
                         String basicChannelDescription) {

        this.FOREGROUND_SERVICE_CHANNEL_NAME = foregroundServiceChannelName;
        this.BASIC_CHANNEL_NAME = basicChannelName;
        this.FOREGROUND_SERVICE_CHANNEL_DESCRIPTION = foregroundServiceChannelDescription;
        this.BASIC_CHANNEL_NAME_DESCRIPTION = basicChannelDescription;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_RESUME);
        intentFilter.addAction(ACTION_RESUME_AUTO_PAUSE);
        this.context = context;
        context.registerReceiver(this, new IntentFilter(intentFilter));
        this.notificationActionButtonListener = notificationActionButtonListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                    context,
                    foregroundServiceChannelName,
                    foregroundServiceChannelDescription,
                    NotificationManager.IMPORTANCE_DEFAULT,
                    NOTIFICATION_CHANNEL_FOREGROUND_SERVICE_ID);

            createNotificationChannel(
                    context,
                    BASIC_CHANNEL_NAME,
                    BASIC_CHANNEL_NAME_DESCRIPTION,
                    NotificationManager.IMPORTANCE_HIGH,
                    NOTIFICATION_CHANNEL_BASIC_ID);
        }
    }

    public void unregisterActionListener() {
        context.unregisterReceiver(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(Context context,
                                          String name,
                                          String description,
                                          int importance,
                                          String channelId) {
        NotificationChannel channel =
                new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public Notification buildNotificationForForegroundService(String title,
                                                              String text,
                                                              boolean isPaused,
                                                              boolean isAutoPaused) {

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent notificationPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(
                context,
                NOTIFICATION_CHANNEL_FOREGROUND_SERVICE_ID);

        Intent pauseIntent;
        NotificationCompat.Action actionButtonPause;
        PendingIntent actionPendingIntent;
        if (isPaused) {
            pauseIntent = new Intent(ACTION_PAUSE);
            actionPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    pauseIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            actionButtonPause = new NotificationCompat.Action.Builder(IconCompat.createWithResource(context, R.drawable.ic_baseline_pause_24)
                    , "", actionPendingIntent).build();
        }
        else {
            pauseIntent = new Intent(ACTION_RESUME);
            actionPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    pauseIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            actionButtonPause = new NotificationCompat.Action.Builder(IconCompat.createWithResource(context, R.drawable.ic_baseline_play_arrow_24)
                    , "", actionPendingIntent).build();
        }


        if (isAutoPaused) {
            Intent autoPauseIntent = new Intent(ACTION_RESUME_AUTO_PAUSE);
            actionPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    autoPauseIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action actionButtonAutoPause = new NotificationCompat.Action.Builder(
                    IconCompat.createWithResource(context,R.drawable.ic_baseline_restore_24),
                    context.getString(R.string.action_session_on), actionPendingIntent).build();
            notificationBuilder.addAction(actionButtonAutoPause);
        }

        return notificationBuilder
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(notificationPendingIntent)
                .addAction(actionButtonPause)
                .setShowWhen(false)
                .build();
    }

    public void changeNotificationForForegroundService(String title,
                                                       String text,
                                                       boolean isPaused,
                                                       boolean isSessionOn) {
        notify(
                context,
                buildNotificationForForegroundService(
                        title,
                        text,
                        isPaused,
                        isSessionOn),
                FOREGROUND_SERVICE_NOTIFICATION_ID);
    }

    public static void notify(Context context, Notification notification, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification);
    }

    public void notifyInstructionTimeout() {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(
                        context,
                        NOTIFICATION_CHANNEL_BASIC_ID);
        notificationBuilder.setContentText(
                        context.getString(
                                R.string.notification_content_text_instruction_timeout));
        notify(
                context,
                notificationBuilder.build(),
                INSTRUCTION_TIMEOUT_NOTIFICATION_ID);
    }

    public void notifyConnectionFail() {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(
                        context,
                        NOTIFICATION_CHANNEL_BASIC_ID);
        notificationBuilder.setContentText(
                context.getString(
                        R.string.notification_content_text_connection_failed));
        notify(
                context,
                notificationBuilder.build(),
                INSTRUCTION_CONNECTION_FAIL_NOTIFICATION_ID);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_PAUSE:
                notificationActionButtonListener.onPauseClicked();
                break;
            case ACTION_RESUME:
                notificationActionButtonListener.onResumeClicked();
                break;
            case ACTION_RESUME_AUTO_PAUSE:
                notificationActionButtonListener.onNewSessionClicked();
                break;
        }
    }
}
