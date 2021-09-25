package com.vamsigutha.saranghae;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.content.Context.CLIPBOARD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "BottomSheetDialog";
    String token;
    TextView myCode;
    Context context;
    ClipboardManager myClipboard;
    ImageButton copyButton;
    Button done;
    EditText partnerCode;
    SharedPreferences sharedPreferences;
    Button connect;
    FrameLayout statsLayout;
    Switch notificationSwitch;
    ConstraintLayout gridLayout;

    public BottomSheetDialog(Context context, Button connect, FrameLayout statsLayout, ConstraintLayout gridLayout){
        this.context =context;
        this.connect = connect;
        this.statsLayout = statsLayout;
        this.gridLayout = gridLayout;
        myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.bottom_sheet_layout,container,false);

        sharedPreferences =  context.getSharedPreferences("token",0);

       myCode = v.findViewById(R.id.mycode);
       copyButton = v.findViewById(R.id.copyButton);
       done = v.findViewById(R.id.done);
       partnerCode = v.findViewById(R.id.partnerCode);
       notificationSwitch = v.findViewById(R.id.notificationSwitch);
       notificationSwitch.setChecked(sharedPreferences.getBoolean("notification",false));

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("notification",isChecked);
                editor.apply();
                if(isChecked){
                    Toast.makeText(context,"Notifications Turned On",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"Notifications Turned Off",Toast.LENGTH_SHORT).show();
                }
            }
        });



       getToken();

       myCode.setOnClickListener(new View.OnClickListener(){

           @Override
           public void onClick(View v) {
               ClipData clip = ClipData.newPlainText("token", token);
               myClipboard.setPrimaryClip(clip);
               Toast.makeText(context,"copied to clipboard",Toast.LENGTH_SHORT).show();
           }
       });

        copyButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ClipData clip = ClipData.newPlainText("token", token);
                myClipboard.setPrimaryClip(clip);
                Toast.makeText(context,"copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });

        done.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("partnerToken",partnerCode.getText().toString());
                editor.apply();
                Toast.makeText(context,"Saved partner token",Toast.LENGTH_SHORT).show();

                connect.setVisibility(View.INVISIBLE);
                statsLayout.setVisibility(View.VISIBLE);
                gridLayout.setVisibility(View.VISIBLE);
                dismiss();
            }
        });



       return v;
    }

    void getToken(){
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

                    }
                });
    }
}
