package com.example.chatit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class ServerConnect extends JobIntentService {
    public ServerConnect() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }
}