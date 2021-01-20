package com.example.systemshortcuts;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;

import androidx.annotation.NonNull;


import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.EventChannel;

/**
 * SystemShortcutsPlugin
 */
public class SystemShortcutsPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {
    private MethodChannel channel;
    private Activity activity;
    private EventChannel.EventSink eventChannel;
    

    public static final String SERVICECMD = "com.android.music.musicservicecommand";

    /**
     * v2 plugin embedding
     */
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(
            binding.getBinaryMessenger(), "system_shortcuts");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        channel = null;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.v("tag ", action + " / " + cmd);
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            Log.v("tag", artist + ":" + album + ":" + track);

            HashMap map = new HashMap<String, Any>();
            map["artist"] = artist;
            map["album"] = album;
            map["track"] = track;
            map["command"] = command;

            if(eventChannel != null){
                eventChannel.success(map);
            }
        }
    };

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        eventChannel = eventSink;

        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.htc.music.metachanged");
        iF.addAction("fm.last.android.metachanged");
        iF.addAction("com.sec.android.app.music.metachanged");
        iF.addAction("com.nullsoft.winamp.metachanged");
        iF.addAction("com.amazon.mp3.metachanged");     
        iF.addAction("com.miui.player.metachanged");        
        iF.addAction("com.real.IMP.metachanged");
        iF.addAction("com.sonyericsson.music.metachanged");
        iF.addAction("com.rdio.android.metachanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.andrew.apollo.metachanged");

        registerReceiver(mReceiver, iF);


    }

    @Override
    public void onCancel(Object o) {
        eventChannel = null;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        SystemShortcutsPlugin instance = new SystemShortcutsPlugin();
        instance.channel = new MethodChannel(registrar.messenger(), "system_shortcuts");
        instance.activity = registrar.activity();
        instance.channel.setMethodCallHandler(instance);
    }

    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "home":
                home();
                break;
            case "back":
                back();
                break;
            case "volDown":
                volDown();
                break;
            case "volUp":
                volUp();
                break;
            case "nextTrack":
                nextTrack();
                break;
            case "lastTrack":
                lastTrack();
                break;
            case "playTrack":
                playTrack();
                break;
            case "pauseTrack":
                pauseTrack();
                break;
            case "orientLandscape":
                orientLandscape();
                break;
            case "orientPortrait":
                orientPortrait();
                break;
            case "wifi":
                wifi();
                break;
            case "checkWifi":
                result.success(checkWifi());
                break;
            case "bluetooth":
                bluetooth();
                break;
            case "checkBluetooth":
                result.success(checkBluetooth());
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void home() {
        this.activity.startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void back() {
        this.activity.onBackPressed();
    }

    private void volDown() {
        AudioManager audioManager = (AudioManager) this.activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
    }

    private void volUp() {
        AudioManager audioManager = (AudioManager) this.activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
    }

    private void nextTrack() {
        AudioManager audioManager = (AudioManager) this.activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        long eventTime = SystemClock.uptimeMillis();

        KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
        audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }

    private void lastTrack() {
        AudioManager audioManager = (AudioManager) this.activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        long eventTime = SystemClock.uptimeMillis();

        KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
        audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }

    private void playTrack() {
        AudioManager audioManager = (AudioManager) this.activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        long eventTime = SystemClock.uptimeMillis();

        KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
        audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }

    private void pauseTrack() {
        AudioManager audioManager = (AudioManager) this.activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        long eventTime = SystemClock.uptimeMillis();

        KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0);
        audioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE, 0);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }

    private void orientLandscape() {
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void orientPortrait() {
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void wifi() {
        WifiManager wifiManager = (WifiManager) this.activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        } else {
            wifiManager.setWifiEnabled(true);
        }
    }

    private boolean checkWifi() {
        WifiManager wifiManager = (WifiManager) this.activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void bluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        } else {
            mBluetoothAdapter.enable();
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private boolean checkBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

}
