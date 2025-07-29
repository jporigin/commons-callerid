package com.origin.commons.callerid.receivers

import android.app.Service
import android.content.Intent
import android.os.IBinder

class CIdForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}