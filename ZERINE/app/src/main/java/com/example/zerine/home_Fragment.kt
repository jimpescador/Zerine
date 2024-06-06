package com.example.zerine
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException
import java.io.InputStream
import java.util.Timer
import java.util.TimerTask
import java.util.UUID


class home_Fragment : Fragment() {

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val TAG = "MainActivity"
    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val bluetoothAdapter: BluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
    private var serverSocket: BluetoothServerSocket? = null
    private lateinit var txtphone: TextView
    lateinit var phone: EditText
    private val mAuth = FirebaseAuth.getInstance()

    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
    }

    private lateinit var myRef: DatabaseReference
    private lateinit var myRef2: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_, container, false)

        viewFlipper = view.findViewById(R.id.viewflipper)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val btnLeft: ImageButton = view.findViewById(R.id.leftbtn)
        val btnRight: ImageButton = view.findViewById(R.id.rightbtn)
        val exitImg: ImageView = view.findViewById(R.id.exitbtn)
        val bpmvalue: EditText = view.findViewById(R.id.BPM_value_edittext)
        val spo2value: EditText = view.findViewById(R.id.O2_value_edittext)
        phone = view.findViewById(R.id.editTextPhone)

        val view2 = inflater.inflate(R.layout.fragment_profile_, container, false)
        txtphone = view2.findViewById(R.id.sosmobile)

        val database = FirebaseDatabase.getInstance("https://database-ea0bd-default-rtdb.asia-southeast1.firebasedatabase.app")
        myRef = database.getReference("sensorValues/currentValue")
        myRef2 = database.getReference("sensorValues/spo2")

        // Set up ValueEventListener to read data and update bpmvalue
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sensorValue = dataSnapshot.getValue(Float::class.java)
                sensorValue?.let {
                    bpmvalue.text = Editable.Factory.getInstance().newEditable("$sensorValue")
                    Log.d(TAG, "Sensor Value: $sensorValue")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

        myRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val spo2Value = dataSnapshot.getValue(String::class.java)
                spo2Value?.let {
                    spo2value.text = Editable.Factory.getInstance().newEditable(it)
                    Log.d(TAG, "SPO2 Value: $it")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read SPO2 value.", error.toException())
            }
        })

        btnLeft.setOnClickListener {
            viewFlipper.showPrevious()
        }

        btnRight.setOnClickListener {
            viewFlipper.showNext()
        }

        exitImg.setOnClickListener {
            showExitDialog()
        }

        /*Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                acceptConnection()
            }
        }, 0, 3000)*/



        return view
    }

    private fun acceptConnection() {
        Thread {
            var socket: BluetoothSocket? = null
            try {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@Thread
                }
                Log.d(TAG, "Listening...")
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothExample", MY_UUID)
                socket = serverSocket?.accept()
                val inputStream: InputStream = socket?.inputStream!!

                val buffer = ByteArray(1024)
                var bytes: Int
                while (inputStream.read(buffer).also { bytes = it } != -1) {
                    val data = String(buffer, 0, bytes)

                    Log.d(TAG, "Received data: $data")

                    // Handle received data here
                    if(data=="1"){
                        //Send message here
                        sendCurrentLocation()
                    }

                }
            } catch (e: IOException) {
                Log.e(TAG, "Error accepting connection: ${e.message}")
            } finally {
               /* try {
                    serverSocket?.close()
                    socket?.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Error closing sockets: ${e.message}")
                }*/
            }
        }.start()
    }


    private fun sendCurrentLocation() {
        if (isAdded && context != null) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val locationMessage = "Zerine Companion \nSeizure Detected! \nLocation: https://maps.google.com?q=$latitude,$longitude"
                        sendSMS(phone.text.toString(), locationMessage)
                    } else {
                        Log.d("Error", "Invalid Location")
                    }
                }
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        /*try {
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT >= 23) {
                smsManager = requireContext().getSystemService(SmsManager::class.java)
            } else {
                smsManager = SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(requireContext(), "Message Sent", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.d("test006", "Error SMS: " + e.message.toString())
        }*/
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(requireContext(), "Message Sent", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Some fields is Empty", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to exit?")
        builder.setPositiveButton("Yes") { _, _ ->
            requireActivity().finishAffinity()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
