package com.example.steammarketbot.core;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.steammarketbot.R;
import com.example.steammarketbot.core.alarmManager.AlarmListener;
import com.example.steammarketbot.core.alarmManager.BotAlarmManager;
import com.example.steammarketbot.core.alarmManager.BotWakeLock;
import com.example.steammarketbot.core.alarmManager.ConnectionInfo;
import com.example.steammarketbot.core.instructions.Instruction;
import com.example.steammarketbot.core.instructions.InstructionResult;
import com.example.steammarketbot.core.instructions.loginInstructions.LoginInstruction;
import com.example.steammarketbot.core.items.Category;
import com.example.steammarketbot.core.items.Item;
import com.example.steammarketbot.core.items.ItemChangeListener;
import com.example.steammarketbot.core.items.ItemsManager;
import com.example.steammarketbot.core.items.Orders;
import com.example.steammarketbot.core.logic.InstructionQueue;
import com.example.steammarketbot.core.logic.InstructionQueueChangeListener;
import com.example.steammarketbot.core.notifications.NotificationActionButtonListener;
import com.example.steammarketbot.core.notifications.Notifications;
import com.example.steammarketbot.core.profileInfo.Wallet;
import com.example.steammarketbot.core.profileInfo.lot.LotForBuyList;
import com.example.steammarketbot.core.profileInfo.lot.LotForSellList;
import com.example.steammarketbot.core.status.StatusChangeListener;
import com.example.steammarketbot.core.status.SteamMarketBotStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class SteamMarketBot extends Service {

    private SettingsManager settingsManager;
    private SteamMarketBotStatus steamMarketBotStatus;
    private com.example.steammarketbot.core.logic.Logic logic;
    private InstructionQueue instructionQueue;
    private BotAlarmManager botAlarmManager;
    private BotWakeLock botWakeLock;
    private Notifications notifications;
    private ConnectionInfo connectionInfo;
    private ItemsManager itemsManager;

    private static String sessionId;
    private static Wallet wallet;
    private static LotForSellList lotForSellList;
    private static LotForBuyList lotForBuyList;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        settingsManager = new SettingsManager(
                getSharedPreferences(
                        SettingsManager.SHARED_PREFERENCE_NAME,
                        MODE_PRIVATE),
                this,
                new Settings());
        notifications = new Notifications(
                this,
                new NotificationsButtons(),
                getString(R.string.notification_foreground_service_channel_name),
                getString(R.string.notification_basic_channel_name),
                getString(R.string.notification_foreground_service_channel_description),
                getString(R.string.notification_basic_channel_description));
        connectionInfo = new ConnectionInfo(
                this,
                new Connection());
        botAlarmManager = new BotAlarmManager(
                this,
                AlarmManager.RTC_WAKEUP,
                new Alarm());
        botWakeLock = new BotWakeLock(
                this,
                settingsManager.getLongSetting(SettingsManager.WakeLockTimeoutMinutes) * 60 * 1000);
        steamMarketBotStatus = new SteamMarketBotStatus(
                getResources(),
                new Status(),
                settingsManager.getLongSetting(SettingsManager.WorkSessionLengthMinutes) * 60 * 1000,
                settingsManager.getIntSetting(SettingsManager.RandomRateForWorkSessionLengthPercent),
                settingsManager.getLongSetting(SettingsManager.PauseLengthMinutes) * 60 * 1000,
                settingsManager.getIntSetting(SettingsManager.RandomRateForPauseLengthPercent),
                settingsManager.getLongSetting(SettingsManager.PauseInCaseOfErrorToMuchRequestsLengthMinutes) * 60 * 1000,
                settingsManager.getIntSetting(SettingsManager.RandomRateForPauseInCaseOfErrorToMuchRequestsLengthPercent));
        logic = new com.example.steammarketbot.core.logic.Logic(
                this,
                settingsManager.getLongSetting(SettingsManager.HttpClientCallTimeoutMilliseconds),
                TimeUnit.MILLISECONDS,
                settingsManager.getStringSetting(SettingsManager.UserAgent),
                settingsManager.getLongSetting(SettingsManager.DefaultInstructionStepDelayMilliseconds),
                settingsManager.getIntSetting(SettingsManager.DefaultInstructionRandomRatePercent),
                settingsManager.getIntSetting(SettingsManager.MaxHttpRequestCount),
                new Logic());
        startForeground(
                Notifications.FOREGROUND_SERVICE_NOTIFICATION_ID,
                notifications.buildNotificationForForegroundService(
                        getString(R.string.status_awaiting_login),
                        "",
                        false,
                        false));
        instructionQueue = new InstructionQueue(new InstructionQueueListener());
        itemsManager = new ItemsManager(new ItemsListener());
        lotForSellList = new LotForSellList(new HashSet<>(), 0);
        lotForBuyList = new LotForBuyList(new HashSet<>(), 0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startSteamMarketBot(String login, String password, String twoFactorCode) {
        instructionQueue.addInstruction(new LoginInstruction(
                0,
                getString(R.string.instruction_login_description),
                login,
                password,
                twoFactorCode));
        itemsManager.loadItems(this);
    }

    private void stopSteamMarketBot() {
        botAlarmManager.cancelAllAlarms();
        botAlarmManager.unregisterAlarmListener();
        connectionInfo.unregisterNetworkCallback();
        notifications.unregisterActionListener();
        logic.cancelExecution();
        logic.saveCookies(this);
        itemsManager.saveItems(this);
        botWakeLock.release();
    }

    private void executeInstruction(Instruction instruction) {
        if (instruction != null) {
            if (System.currentTimeMillis() >= instruction.getTimeWindowStarts())
                if (checkConditionsMatchForRunNextInstructionAlarm(instruction instanceof LoginInstruction)) {
                    botWakeLock.acquire();
                    logic.executeInstruction(instruction);
                } else {
                    if (instruction.getTimeWindowStarts() != instruction.getTimeWindowEnds())
                        botAlarmManager.setInstructionAlarm(
                                instruction.getTimeWindowStarts(),
                                instruction.getTimeWindowEnds());
                    else
                        botAlarmManager.setInstructionAlarm(instruction.getTimeWindowStarts());
                }
        }
    }

    private boolean checkConditionsMatchForRunNextInstructionAlarm(boolean loginInstruction) {
        if (loginInstruction)
            return steamMarketBotStatus.getStatus() == SteamMarketBotStatus.STATUS_STATUS_AWAITING_LOGIN
                    && !logic.isBusy();
        else
            return steamMarketBotStatus.getStatus() == SteamMarketBotStatus.STATUS_WORKING
                    && !logic.isBusy();
    }

    private ArrayList<Instruction> buildMarketInstructions() {
        return new ArrayList<>();
    }

    private class Settings implements SettingsManager.OnSettingsChangeListener {

        @Override
        public void onSettingChange(String key, Object value) {
            switch (key) {
                case SettingsManager.CheckYourListingItemsOnlyOrderSeconds:
                    break;

                case SettingsManager.WaitBeforeCheckNextItemMilliseconds:
                    break;
                case SettingsManager.RandomRateForWaitBeforeCheckNextItemPercent:
                    break;

                case SettingsManager.WorkSessionLengthMinutes:
                    steamMarketBotStatus.setWorkSessionLength((long) value * 60 * 1000);
                    break;
                case SettingsManager.RandomRateForWorkSessionLengthPercent:
                    steamMarketBotStatus.setRandomRateForWorkSessionLength((int) value);
                    break;
                case SettingsManager.PauseLengthMinutes:
                    steamMarketBotStatus.setPauseLength((long) value * 60 *1000);
                    break;
                case SettingsManager.RandomRateForPauseLengthPercent:
                    steamMarketBotStatus.setRandomRateForPauseLength((int) value);
                    break;

                case SettingsManager.PauseInCaseOfErrorToMuchRequestsLengthMinutes:
                    steamMarketBotStatus.setPauseInCaseOfErrorToMuchRequestsLength((long) value * 60 * 1000);
                    break;
                case SettingsManager.RandomRateForPauseInCaseOfErrorToMuchRequestsLengthPercent:
                    steamMarketBotStatus.setRandomRateForPauseInCaseOfToMuchRequests((int) value);
                    break;

                case SettingsManager.CheckInventoryOnlyWhilePause:
                    break;
                case SettingsManager.CheckInventoryEveryMinutes:
                    break;
                case SettingsManager.RandomRateForCheckInventoryEveryPercent:
                    break;
                case SettingsManager.WaitBeforeSellNextItemSeconds:
                    break;
                case SettingsManager.RandomRateForWaitBeforeSellNextItemPercent:
                    break;

                case SettingsManager.HttpClientCallTimeoutMilliseconds:
                    break;
                case SettingsManager.UserAgent:
                    logic.setUserAgent((String) value);
                    break;

                case SettingsManager.DefaultInstructionStepDelayMilliseconds:
                    logic.setDefaultStepDelay((long) value);
                    break;
                case SettingsManager.DefaultInstructionRandomRatePercent:
                    logic.setDefaultRandomRate((int) value);
                    break;

                case SettingsManager.UseOnlyWifi:
                    if (connectionInfo.isConnected()) {
                        if ((boolean) value)
                            steamMarketBotStatus.setNetConnected(connectionInfo.isUnMetered());
                        else
                            steamMarketBotStatus.setNetConnected(true);
                    }
                    else
                        steamMarketBotStatus.setNetConnected(false);
                    break;

                case SettingsManager.WakeLockTimeoutMinutes:
                    botWakeLock.setTimeout((long) value * 60 * 1000);
                    break;

                case SettingsManager.NewAttemptDelaySeconds:
                    break;
            }
        }
    }

    private class Logic implements LogicCallbackListener {

        @Override
        public void onInstructionTimeout(int id, int attempt) {
            if (attempt > 2) {
                steamMarketBotStatus.setPause();
                instructionQueue.getInstructionById(id).setAttempt(0);
                notifications.notifyInstructionTimeout();
            }
            else {
                botAlarmManager.setInstructionAlarm(
                        System.currentTimeMillis() +
                                (long) settingsManager.getIntSetting(
                                        SettingsManager.NewAttemptDelaySeconds) *
                                        60 * 1000);
                instructionQueue.getInstructionById(id).setAttempt(attempt + 1);
            }
            botWakeLock.release();
        }

        @Override
        public void onConnectionFailed(int id, int attempt) {
            if (attempt > 2) {
                steamMarketBotStatus.setPause();
                instructionQueue.getInstructionById(id).setAttempt(0);
                notifications.notifyConnectionFail();
            }
            else {
                botAlarmManager.setInstructionAlarm(
                        System.currentTimeMillis() +
                                (long) settingsManager.getIntSetting(
                                        SettingsManager.NewAttemptDelaySeconds) *
                                        60 * 1000);
                instructionQueue.getInstructionById(id).setAttempt(attempt + 1);
            }
            botWakeLock.release();
        }

        @Override
        public void onInstructionFinish(int id, InstructionResult instructionResult) {
            instructionQueue.removeInstructionById(id);
            botWakeLock.release();
            //TODO добавить новую инструкцию
        }

        @Override
        public void onUserDataNeeded(int id, int code) {

        }

        @Override
        public void onStep(int id, String stepDescription) {
            notifications.changeNotificationForForegroundService(
                    instructionQueue.getInstructionById(id).getInstructionDescription(),
                    stepDescription,
                    steamMarketBotStatus.isPause(),
                    steamMarketBotStatus.isSessionOn());
        }
    }

    private class InstructionQueueListener implements InstructionQueueChangeListener {

        @Override
        public void onQueueChanged(Instruction nextInstruction) {
          if(checkConditionsMatchForRunNextInstructionAlarm(nextInstruction instanceof LoginInstruction))
              executeInstruction(nextInstruction);
        }

        @Override
        public void onRemoveInstructionFromQueue(Instruction removedInstruction) {
            if (logic.isBusy() && logic.getRunningInstructionId() == removedInstruction.getId())
                logic.cancelExecution();
        }
    }

    private class ItemsListener implements ItemChangeListener {


        @Override
        public void onItemAdded(Item item, String categoryName) {
            //TODO коррекция инструкции
        }

        @Override
        public void onItemRemoved(Item removedItem, String categoryName) {
            //TODO коррекция инструкции
        }

        @Override
        public void onItemPropertiesChanged(Item item) {
            //TODO коррекция инструкции
        }

        @Override
        public void onItemUserDataChanged(Item item, String categoryName) {
            //TODO коррекция инструкции
        }

        @Override
        public void onItemOrdersChanged(Item item, Orders orders) {

        }

        @Override
        public void onCategoryRemoved(String name) {
            //TODO удаление инструкции
        }

        @Override
        public void onCategoryPropertiesChanged(Category category) {
            //TODO коррекция инструкций
        }

        @Override
        public void onCategoryNameChanged(String oldName, String newName) {

        }
    }

    private class NotificationsButtons implements NotificationActionButtonListener {

        @Override
        public void onPauseClicked() {
            steamMarketBotStatus.setPause();
        }

        @Override
        public void onResumeClicked() {
            steamMarketBotStatus.setResume();
        }

        @Override
        public void onNewSessionClicked() {
            steamMarketBotStatus.setSessionOn();
        }
    }

    private class Status implements StatusChangeListener {


        @Override
        public void onLoggedIn() {
            instructionQueue.addInstructions(buildMarketInstructions());
        }

        @Override
        public void onLoggedOut() {
            instructionQueue.clear();
            botAlarmManager.cancelAllAlarms();
        }

        @Override
        public void onPause() {
            botAlarmManager.cancelAllAlarms();
            if (logic.isBusy())
                logic.cancelExecution();
            //TODO event to activity
        }

        @Override
        public void onResume() {
            if (checkConditionsMatchForRunNextInstructionAlarm(steamMarketBotStatus.isLoggedIn()))
                executeInstruction(instructionQueue.getNextInstruction());
            //TODO event to activity
        }

        @Override
        public void onSessionStarts(long sessionEndsMin, long sessionEndsMax) {
            if (checkConditionsMatchForRunNextInstructionAlarm(false))
                executeInstruction(instructionQueue.getNextInstruction());
            botAlarmManager.setSessionOffAlarm(sessionEndsMin, sessionEndsMax);
            botAlarmManager.cancelSessionOnAlarm();
        }

        @Override
        public void onSessionLengthChanged(long sessionEndsMin, long sessionEndsMax) {
            if (steamMarketBotStatus.isSessionOn())
                botAlarmManager.setSessionOffAlarm(sessionEndsMin, sessionEndsMax);
        }

        @Override
        public void onSessionEnds(long sessionStartsMin, long sessionStartsMax) {
            botAlarmManager.setSessionOnAlarm(sessionStartsMin, sessionStartsMax);
        }

        @Override
        public void onSessionPauseLengthChanged(long sessionStartsMin, long sessionStartsMax) {
            if (!steamMarketBotStatus.isSessionOn())
                botAlarmManager.setSessionOnAlarm(sessionStartsMin, sessionStartsMin);
        }

        @Override
        public void onPauseErrors(long pauseErrorsEndsMin, long pauseErrorsEndsMax) {
            botAlarmManager.setResumeErrorsAlarm(pauseErrorsEndsMin, pauseErrorsEndsMax);
        }

        @Override
        public void onPauseErrorsLengthChanged(long pauseErrorsEndsMin, long pauseErrorsEndsMax) {
            if (steamMarketBotStatus.isPauseErrors())
                botAlarmManager.setResumeErrorsAlarm(pauseErrorsEndsMin, pauseErrorsEndsMax);
        }

        @Override
        public void onStatusDescriptionChanged(String titleStatus, String statusDescription) {
            notifications.changeNotificationForForegroundService(titleStatus, statusDescription,
                    steamMarketBotStatus.isPause(), steamMarketBotStatus.isSessionOn());
        }

        @Override
        public void onConnectionRetrieve() {
            if (instructionQueue.size() > 0) {
                if (checkConditionsMatchForRunNextInstructionAlarm(instructionQueue.getNextInstruction() instanceof LoginInstruction))
                    executeInstruction(instructionQueue.getNextInstruction());
            }
        }

        @Override
        public void onConnectionLost() {
        }
    }

    private class Alarm implements AlarmListener {

        @Override
        public void onAlarmExecuteNextInstruction() {
            executeInstruction(instructionQueue.getNextInstruction());
        }

        @Override
        public void onAlarmSessionOn() {
            steamMarketBotStatus.setSessionOn();
        }

        @Override
        public void onAlarmSessionOff() {
            steamMarketBotStatus.setSessionOff();
        }

        @Override
        public void onAlarmErrorsResume() {

        }
    }

    private class Connection implements ConnectionInfo.ConnectionStateChangeListener {


        @Override
        public void onConnectionStateChanged(boolean isConnected, boolean isWifi) {
            if (isConnected) {
                if (settingsManager.getBooleanSetting(SettingsManager.UseOnlyWifi))
                    steamMarketBotStatus.setNetConnected(isWifi);
                else
                    steamMarketBotStatus.setNetConnected(true);
            }
            else
                steamMarketBotStatus.setNetConnected(false);
        }
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static Wallet getWallet() {
        return wallet;
    }

    public static LotForSellList getLotForSellList() {
        return lotForSellList;
    }

    public static LotForBuyList getLotForBuyList() {
        return lotForBuyList;
    }
}