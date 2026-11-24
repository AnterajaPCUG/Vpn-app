package com.contoh.vpnapp

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log

class SimpleVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_DISCONNECT -> {
                stopVpnInterface()
                stopSelf()
                START_NOT_STICKY
            }
            else -> {
                startVpnInterface()
                START_STICKY
            }
        }
    }

    private fun startVpnInterface() {
        if (vpnInterface != null) {
            Log.d(TAG, "Antarmuka VPN sudah aktif.")
            return
        }

        try {
            val builder = Builder()
                .setSession(getString(R.string.app_name))
                .addAddress("10.66.0.2", 24)
                .addDnsServer("1.1.1.1")
                .addRoute("0.0.0.0", 0)
                .setMtu(1400)

            vpnInterface = builder.establish()
            Log.d(TAG, "Antarmuka VPN berhasil diinisialisasi.")
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menjalankan VpnService: ${e.message}", e)
            stopVpnInterface()
            stopSelf()
        }
    }

    private fun stopVpnInterface() {
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menutup vpnInterface: ${e.message}", e)
        } finally {
            vpnInterface = null
            Log.d(TAG, "Antarmuka VPN ditutup.")
        }
    }

    override fun onDestroy() {
        stopVpnInterface()
        super.onDestroy()
    }

    override fun onRevoke() {
        stopVpnInterface()
        super.onRevoke()
        stopSelf()
    }

    companion object {
        const val TAG = "SimpleVpnService"
        const val ACTION_CONNECT = "com.contoh.vpnapp.action.CONNECT"
        const val ACTION_DISCONNECT = "com.contoh.vpnapp.action.DISCONNECT"
    }
}
