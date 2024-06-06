package com.example.zerine

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.util.*

class Foregroundservices : Service() {

    companion object {
        private const val CHANNEL_ID = "ForegroundServiceChannel"
        private const val TAG = "ForegroundServices"
        private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var serverSocket: BluetoothServerSocket? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var phoneNumber: String? = null

    override fun onCreate() {
        super.onCreate()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    Log.d(TAG, "Foreground Service is running...")
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                    Log.e(TAG, "Thread interrupted", e)
                }
            }
        }

        createNotificationChannel()
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentText("Foreground Service running")
            .setContentTitle("ZERINE")
            .setSmallIcon(R.drawable.logo) // Add your own icon here
            .build()
        startForeground(1001, notification)

        fetchPhoneNumberFromFirebase()

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                acceptConnection()
            }
        }, 0, 3000)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun acceptConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            var socket: BluetoothSocket? = null
            try {
                if (ActivityCompat.checkSelfPermission(this@Foregroundservices, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Bluetooth connect permission not granted")
                    return@launch
                }
                Log.d(TAG, "Listening...")
                serverSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord("BluetoothExample", MY_UUID)
                socket = serverSocket?.accept()
                val inputStream: InputStream? = socket?.inputStream

                val buffer = ByteArray(1024)
                var bytes: Int
                while (inputStream?.read(buffer).also { bytes = it ?: -1 } != -1) {
                    val data = String(buffer, 0, bytes)
                    Log.d(TAG, "Received data: $data")
                    if (data == "1") {
                        sendCurrentLocation()
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error accepting connection: ${e.message}")
            } finally {
                /*try {
                    serverSocket?.close()
                    socket?.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Error closing sockets: ${e.message}")
                }*/
            }
        }
    }

    private fun sendCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permissions not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val locationMessage = "Zerine Companion \nSeizure Detected! \nLocation: https://maps.google.com?q=$latitude,$longitude"
                sendSMS(phoneNumber, locationMessage)
            } else {
                Log.d(TAG, "Invalid Location")
            }
        }
    }

    private fun sendSMS(phoneNumber: String?, message: String) {
        try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d(TAG, "SMS sent successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending SMS: ${e.message}", e)
        }
    }

    private fun fetchPhoneNumberFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("EContacts").document(userId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                phoneNumber = documentSnapshot.getString("eContactsMobile")
                Log.d(TAG, "Phone number fetched: $phoneNumber")
            } else {
                Log.d(TAG, "User data not found in Firestore.")
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error fetching phone number from Firestore: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }
}
