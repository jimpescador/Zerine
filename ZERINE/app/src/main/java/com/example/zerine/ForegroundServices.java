package com.example.zerine;

import static android.system.Os.accept;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ForegroundServices extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "ForegroundServices";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");; // Replace this with your generated UUID
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket serverSocket = null;
    private FusedLocationProviderClient fusedLocationClient;
    private String phoneNumber;

    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {
            while (true) {
                //Log.d(TAG, "Foreground Service is running...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Thread interrupted", e);
                }
            }
        }).start();

        createNotificationChannel();
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentText("Foreground Service running")
                .setContentTitle("ZERINE")
                .setSmallIcon(R.drawable.logo) // Add your own icon here
                .build();
        startForeground(1001, notification);

        /*phoneNumber = intent.getStringExtra("data_key");
        if (phoneNumber != null) {
            Log.d(TAG, "Received data: " + phoneNumber);
            // Use the data as needed
        }*/

        fetchPhoneNumberFromFirebase();



        // Schedule acceptConnection to run every 3 seconds
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                acceptConnection();
            }
        }, 0, 3000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void acceptConnection() {
        new Thread(() -> {
            BluetoothSocket socket = null;
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Log.d(TAG, "Foreground: Listening...");

                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothExample", MY_UUID);
                socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();

                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = inputStream.read(buffer)) != -1) {
                    String data = new String(buffer, 0, bytes);
                    Log.d(TAG, "Received data: " + data);

                    if ("1".equals(data)) {
                        sendCurrentLocation();
                    } else if ("2".equals(data)) {
                        sendLocationFall();
                    }
                }
            }catch (IOException e) {
                Log.e(TAG, "Error accepting connection: " + e.getMessage(), e);
                try {
                    Thread.sleep(2000); // Retry delay
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    Log.e(TAG, "Thread interrupted during sleep", ie);
                }
            } finally {

                /*try {
                    if (serverSocket != null) serverSocket.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing sockets: " + e.getMessage(), e);
                }*/
            }
        }).start();
    }

    /*private void acceptConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothSocket socket = null;
                try {
                    if (ActivityCompat.checkSelfPermission(ForegroundServices.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Log.d(TAG, "Listening...");
                    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothExample", MY_UUID);
                    socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();

                    byte[] buffer = new byte[1024];
                    int bytes;
                    while ((bytes = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, bytes);

                        Log.d(TAG, "Received data: " + data);

                        // Handle received data here
                        if ("1".equals(data)) {
                            // Send message here
                            sendCurrentLocation();
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error accepting connection: " + e.getMessage());
                } finally {
                /*try {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error closing sockets: " + e.getMessage());
                }
                }
            }
        }).start();
    }*/

    private void sendLocationFall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String locationMessage = "Zerine Companion \nPossible Fall Detected! \nLocation: " + latitude + "," + longitude + "\nPlease enter coordinates in Google Maps";
                sendSMS(phoneNumber, locationMessage);
            } else {
                Log.d(TAG, "Invalid Location");
            }
        });
    }


    private void sendCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String locationMessage = "Zerine Companion \nSeizure Detected! \nLocation Coordinates: " + latitude + "," + longitude + "\nPlease enter the coordinates in Google Maps";
                sendSMS(phoneNumber, locationMessage);
            }  else {
                Log.d(TAG, "Invalid Location");
            }
        });
    }

    /*private void shortenURL(String longURL) {
        String accessToken = "70e40d03314a36658a517a8922395abce9cd8721";
        String endpoint = "https://api-ssl.bitly.com/v4/shorten";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("long_url", longURL);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, endpoint, requestBody,
                response -> {
                    try {
                        String shortURL = response.getString("link");
                        String locationMessage = "Zerine Companion \nPossible Seizure Detected! \nLocation: " + shortURL;
                        sendSMS(phoneNumber, locationMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        sendSMS(phoneNumber, "Zerine Companion \nPossible Seizure Detected! \nLocation: " + longURL);
                    }
                },
                error -> {
                    error.printStackTrace();
                    sendSMS(phoneNumber, "Zerine Companion \nPossible Seizure Detected! \nLocation: " + longURL);
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(request);
    }*/


    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                smsManager = getSystemService(SmsManager.class);
            } else {
                smsManager = SmsManager.getDefault();
            }
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d(TAG, "SMS sent successfully");
            Toast.makeText(this, "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error sending SMS: " + e.getMessage(), e);
            showToastOnUiThread("SMS not sent");
        }
    }
    private void showToastOnUiThread(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchPhoneNumberFromFirebase() {
        // Assuming you have the user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Specify the collection path and document ID
        DocumentReference docRef = db.collection("EContacts").document(userId);

        // Query the Firestore to get the phone number
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                phoneNumber = documentSnapshot.getString("eContactsMobile");
                Log.d(TAG, "Phone number fetched: " + phoneNumber);
            } else {
                Log.d(TAG, "User data not found in Firestore.");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching phone number from Firestore: " + e.getMessage());
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
