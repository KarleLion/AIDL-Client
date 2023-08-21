package com.example.client;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.client.databinding.ActivityMainBinding;
import com.example.server.IRemoteService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity-";

    private ActivityMainBinding binding;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "onCreate: ");
        bindService();

        binding.btnGetRect.setOnClickListener(v -> {
            Log.d(TAG, "binding.btnGetRect is clicked");

            performCallback(() -> {
                Rect rect = remoteService.getRect();
                binding.tvTip.setText(rect == null ? "rect = null"
                        : "-->get Rect  left: " + rect.left
                        + ", top: " + rect.top
                        + ", right: " + rect.right
                        + ", bottom: " + rect.bottom);
            });
        });

        binding.btnSaveRect.setOnClickListener(v -> {
            Log.d(TAG, "binding.btnGetRect is clicked");

            performCallback(() -> {
                Rect rect = new Rect(10, 11, 12, 13);
                Bundle bundle = new Bundle();
                bundle.putParcelable("rect", rect);
                remoteService.saveRect(bundle);

                binding.tvTip.setText("-->save Rect  left: " + rect.left
                        + ", top: " + rect.top
                        + ", right: " + rect.right
                        + ", bottom: " + rect.bottom);
            });
        });

        binding.btnGetPid.setOnClickListener(v -> {
            Log.d(TAG, "binding.btnGetRect is clicked");

            performCallback(() -> {
                int pid = remoteService.getPid();
                binding.tvTip.setText("pid: " + pid);
            });
        });

    }

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

    void performCallback(Callback callback) {
        try {
            if (isBound && remoteService != null) {
                callback.onCallback();
            } else if (!isBound) {
                Log.d(TAG, "isBound = false");
            } else {
                Log.d(TAG, "remoteService = null");
            }
        } catch (RemoteException e) {
            Log.d(TAG, "catch RemoteException");
            e.printStackTrace();
        }

    }

    interface Callback {
        void onCallback() throws RemoteException;
    }
}