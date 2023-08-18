package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.example.server.IRemoteService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity-";

    private IRemoteService remoteService;

    boolean isBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
            remoteService = IRemoteService.Stub.asInterface(iBinder);
            isBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");
        bindService();

        tvTip = findViewById(R.id.tv_Tip);
        tvTip.setOnClickListener(v -> {
            if (isBound && remoteService != null) {
                try {
                    int pid = remoteService.getPid();
                    ((TextView) findViewById(R.id.tv_Tip)).setText("Process id: " + pid);
                } catch (RemoteException e) {
                    Log.d(TAG, "catch RemoteException");
                    e.printStackTrace();
                }
            }
        });
    }

    TextView tvTip;

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop: ");
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void bindService() {
        Log.d(TAG, "bindService: ");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.server", "com.example.server.RemoteService"));
        intent.setAction("com.example.server.REMOTE_SERVICE");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}