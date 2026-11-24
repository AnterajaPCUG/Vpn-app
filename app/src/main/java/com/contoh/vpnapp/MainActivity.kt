package com.contoh.vpnapp

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isConnected = false
    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button

    private val vpnPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                launchVpnService()
            } else {
                statusText.text = "Izin VPN ditolak oleh pengguna."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        statusText = TextView(this).apply {
            text = getString(R.string.status_disconnected)
            textSize = 18f
            gravity = Gravity.CENTER
        }

        toggleButton = Button(this).apply {
            text = getString(R.string.start_vpn)
            setOnClickListener {
                if (!isConnected) {
                    prepareAndStartVpn()
                } else {
                    stopVpn()
                }
            }
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            addView(statusText)
            addView(toggleButton, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
            })
        }

        setContentView(rootLayout)
    }

    private fun prepareAndStartVpn() {
        val prepareIntent = VpnService.prepare(this)
        if (prepareIntent != null) {
            vpnPermissionLauncher.launch(prepareIntent)
        } else {
            launchVpnService()
        }
    }

    private fun launchVpnService() {
        val intent = Intent(this, SimpleVpnService::class.java).apply {
            action = SimpleVpnService.ACTION_CONNECT
        }
        startService(intent)
        isConnected = true
        statusText.text = getString(R.string.status_connected)
        toggleButton.text = getString(R.string.stop_vpn)
    }

    private fun stopVpn() {
        val intent = Intent(this, SimpleVpnService::class.java).apply {
            action = SimpleVpnService.ACTION_DISCONNECT
        }
        startService(intent)
        isConnected = false
        statusText.text = getString(R.string.status_disconnected)
        toggleButton.text = getString(R.string.start_vpn)
    }
}
