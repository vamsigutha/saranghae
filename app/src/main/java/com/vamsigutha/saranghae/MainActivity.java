package com.vamsigutha.saranghae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import soup.neumorphism.NeumorphCardView;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    private static final String TAG = "MainActivity";
    String token;
    String partnerToken;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button connect;
    FrameLayout statsLayout;
    Button sentButton;
    Button receivedButton;
    SwipeRefreshLayout refreshLayout;
    ConstraintLayout gridLayout;
    NeumorphCardView cardView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startLoginActivity();
        }else{

            IntentFilter iF = new IntentFilter();

            // Read action when music player changed current song
            // I just try it with stock music player form android

            // stock music player
            iF.addAction("com.android.music.metachanged");

            // MIUI music player
            iF.addAction("com.miui.player.metachanged");

            // HTC music player
            iF.addAction("com.htc.music.metachanged");

            // WinAmp
            iF.addAction("com.nullsoft.winamp.metachanged");

            // MyTouch4G
            iF.addAction("com.real.IMP.metachanged");

            iF.addAction("com.kapp.youtube.metachanged");

            registerReceiver(mReceiver, iF);



            connect = findViewById(R.id.connect);
            statsLayout = findViewById(R.id.statsLayout);
            sentButton = findViewById(R.id.sent);
            receivedButton = findViewById(R.id.received);
            refreshLayout = findViewById(R.id.refreshLayout);
            gridLayout = findViewById(R.id.gridLayout);
            cardView = findViewById(R.id.pressed_card);


            sharedPreferences =  getSharedPreferences("token",0);

            partnerToken = sharedPreferences.getString("partnerToken",null);

            sentButton.setText("❤ Sent  "+sharedPreferences.getInt("sentHearts",0));
            receivedButton.setText("❤ Received  "+sharedPreferences.getInt("receivedHearts",0));

            if(partnerToken!=null){
                connect.setVisibility(View.INVISIBLE);
                statsLayout.setVisibility(View.VISIBLE);
                gridLayout.setVisibility(View.VISIBLE);
            }else{
                connect.setVisibility(View.VISIBLE);
                statsLayout.setVisibility(View.INVISIBLE);
                gridLayout.setVisibility(View.INVISIBLE);
            }

            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    sentButton.setText("❤ Sent  "+sharedPreferences.getInt("sentHearts",0));
                    receivedButton.setText("❤ Received  "+sharedPreferences.getInt("receivedHearts",0));
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(),"Stats updated",Toast.LENGTH_SHORT).show();
                }
            });

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            token = task.getResult();

                            Log.d(TAG, token);
//                            Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        }
                    });

            scheduleAlarm();


        }

    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {

            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.d("mIntentReceiver.on ", action + " / " + cmd);
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            Log.d("Music", artist + ":" + album + ":" + track);
        }};

    public void onCardClick(View view){


    }





    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY

//        Calendar cur_cal = new GregorianCalendar();
//        cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

//        Calendar cal = new GregorianCalendar();
//        cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
//        cal.set(Calendar.HOUR_OF_DAY, 15);
//        cal.set(Calendar.MINUTE, 5);
//        cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
//        cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
//        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
//        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 60 * 24, pIntent);
    }

    public void connectButton(View view){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.getApplicationContext(),connect,statsLayout,gridLayout);
        bottomSheetDialog.show(getSupportFragmentManager(),TAG);
    }

    public void sendButton(View view){
        LottieAnimationView heartView = (LottieAnimationView) view;
        partnerToken = sharedPreferences.getString("partnerToken",null);
        if(partnerToken!=null){
            heartView.playAnimation();

            cardView.setShadowColorDark(getResources().getColor(R.color.color_accent1));
            cardView.setShadowColorLight(getResources().getColor(R.color.color_accent1));
            cardView.setShapeType(0);
            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            cardView.setShadowColorDark(getResources().getColor(R.color.color_accent));
                            cardView.setShadowColorLight(getResources().getColor(R.color.background));
                            cardView.setShapeType(1);
                        }
                    },
                    300);


            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                    partnerToken,
                    "Hey Babe",
                    "I love you ❤",
                    getApplicationContext(),
                    MainActivity.this);
            notificationsSender.SendNotifications();

            editor = sharedPreferences.edit();
            editor.putInt("sentHearts",sharedPreferences.getInt("sentHearts",0)+1);
            editor.apply();

            sentButton.setText("❤ Sent  "+sharedPreferences.getInt("sentHearts",0));

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {100, 300, 300, 300};
            v.vibrate(pattern, -1);

        }else{
            Toast.makeText(this, "Invalid partner token", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendLovely(View view){

        LottieAnimationView lovelyView = (LottieAnimationView) view;
        lovelyView.playAnimation();
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                partnerToken,
                "Mood",
                "Feeling lovely 😍" ,
                getApplicationContext(),
                MainActivity.this);
        notificationsSender.SendNotifications();

    }

    public void sendKiss(View view){
        LottieAnimationView kissView = (LottieAnimationView) view;
        kissView.playAnimation();
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                partnerToken,
                "Mood",
                "I wanna kiss you 😘" ,
                getApplicationContext(),
                MainActivity.this);
        notificationsSender.SendNotifications();
    }

    public void sendCool(View view){
        LottieAnimationView coolView = (LottieAnimationView) view;
        coolView.playAnimation();
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                partnerToken,
                "Mood",
                "Feeling cool 😎" ,
                getApplicationContext(),
                MainActivity.this);
        notificationsSender.SendNotifications();
    }

    public void sendCrying(View view){
        LottieAnimationView cryingView = (LottieAnimationView) view;
        cryingView.playAnimation();
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                partnerToken,
                "Mood",
                "Missing you 😭" ,
                getApplicationContext(),
                MainActivity.this);
        notificationsSender.SendNotifications();
    }

    public void sendSad(View view){
        LottieAnimationView sadView = (LottieAnimationView) view;
        sadView.playAnimation();
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                partnerToken,
                "Mood",
                "Feeling sad 😢" ,
                getApplicationContext(),
                MainActivity.this);
        notificationsSender.SendNotifications();
    }

    public void sendSmile(View view){
        LottieAnimationView smileView = (LottieAnimationView) view;
        smileView.playAnimation();
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                partnerToken,
                "Mood",
                "Broad smile 🙂" ,
                getApplicationContext(),
                MainActivity.this);
        notificationsSender.SendNotifications();
    }



    private void startLoginActivity(){
        startActivity(new Intent(this, Login.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null){
            startLoginActivity();
            return;
        }
    }
}