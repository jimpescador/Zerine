package com.example.zerine

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference

class TriggerEdit : AppCompatActivity() {

    private lateinit var numberInput1: EditText
    private lateinit var numberInput2: EditText
    private lateinit var numberInput3: EditText
    private lateinit var numberInput4: EditText
    private lateinit var auth: FirebaseAuth
    private val SHARED_DOCUMENT_ID = "sharedTriggerValues"
    private var selectedEditText: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_trigger)

        numberInput1 = findViewById(R.id.numberInput)
        numberInput2 = findViewById(R.id.alertnumberInput)
        numberInput3 = findViewById(R.id.numberInput2)
        numberInput4 = findViewById(R.id.alertnumberInput2)
        val incrementButton = findViewById<Button>(R.id.incrementButton)
        val decrementButton = findViewById<Button>(R.id.decrementButton)

        val aincrementButton = findViewById<Button>(R.id.aincrementButton)
        val adecrementButton = findViewById<Button>(R.id.adecrementButton)

        val saveButton = findViewById<Button>(R.id.saveButton)
        auth = FirebaseAuth.getInstance()

        incrementButton.setOnClickListener {
            selectedEditText?.let { editText ->
                incrementNumber(editText)


            }
        }

        decrementButton.setOnClickListener {
            selectedEditText?.let { editText ->
                decrementNumber(editText)
            }
        }

        aincrementButton.setOnClickListener {
            selectedEditText?.let { editText ->
                incrementNumber(editText)


            }
        }
        adecrementButton.setOnClickListener {
            selectedEditText?.let { editText ->
                incrementNumber(editText)


            }
        }

        numberInput1.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                selectedEditText = numberInput1
            }
        }

        numberInput2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                selectedEditText = numberInput2
            }
        }

        numberInput3.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                selectedEditText = numberInput3
            }
        }

        numberInput4.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                selectedEditText = numberInput4
            }
        }

        saveButton.setOnClickListener { saveToFirebase() }
        updateUIWithSavedData()
    }

    private fun incrementNumber(numberInput: EditText) {
        val currentNumber = numberInput.text.toString().toDoubleOrNull() ?: 0.0
        numberInput.setText((currentNumber + 1).toString())
    }

    private fun decrementNumber(numberInput: EditText) {
        val currentNumber = numberInput.text.toString().toDoubleOrNull() ?: 0.0
        numberInput.setText((currentNumber - 1).toString())
    }

    private fun saveToFirebase() {
        val db = FirebaseFirestore.getInstance()

        val numberToSave1 = numberInput1.text.toString().toDoubleOrNull() ?: 0.0
        val numberToSave2 = numberInput2.text.toString().toDoubleOrNull() ?: 0.0
        val numberToSave3 = numberInput3.text.toString().toDoubleOrNull() ?: 0.0
        val numberToSave4 = numberInput4.text.toString().toDoubleOrNull() ?: 0.0

        if (numberToSave1.isNaN() || numberToSave2.isNaN() || numberToSave3.isNaN() || numberToSave4.isNaN()) {
            // Handle invalid input (NaN)
            return
        }

        val dataToSave = mapOf("Warning" to numberToSave1, "Alert" to numberToSave2, "Warning_Low" to numberToSave3, "Alert_Low" to numberToSave4)

        // Access Firestore collection with the shared document
        val sharedDocument = db.collection("TriggerValues").document(SHARED_DOCUMENT_ID)

        sharedDocument
            .set(dataToSave)
            .addOnSuccessListener {
                println("DocumentSnapshot added with ID: ${sharedDocument.id}")

                // Clear the input fields


                // Display a toast indicating successful save
                Toast.makeText(this, "Trigger Values saved", Toast.LENGTH_SHORT).show()

                // Update the UI with the saved data

            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
                // Handle the error, e.g., show a message to the user
            }
    }

    private fun updateUIWithSavedData() {
        val db = FirebaseFirestore.getInstance()
        val sharedDocument = db.collection("TriggerValues").document(SHARED_DOCUMENT_ID)

        sharedDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val savedNumber1 = documentSnapshot.getLong("Warning")?.toInt() ?: 0
                    val savedNumber2 = documentSnapshot.getLong("Alert")?.toInt() ?: 0
                    val savedNumber3 = documentSnapshot.getLong("Warning_Low")?.toInt() ?: 0
                    val savedNumber4 = documentSnapshot.getLong("Alert_Low")?.toInt() ?: 0

                    // Set the retrieved values to the EditText fields
                    numberInput1.setText(savedNumber1.toString())
                    numberInput2.setText(savedNumber2.toString())
                    numberInput3.setText(savedNumber3.toString())
                    numberInput4.setText(savedNumber4.toString())

                    // You can also trigger any other UI updates or actions here
                    // For example, you can update TextViews, display a toast, etc.
                } else {
                    // If the document doesn't exist, set default values to the EditText fields
                    numberInput1.setText("106")
                    numberInput2.setText("120")
                    numberInput3.setText("50")
                    numberInput4.setText("35")
                    println("Document does not exist")

                    // Since there are default values, you might want to save them to Firestore here.
                    // Example:
                    // saveToFirebase()
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching document: $e")
            }
    }









}

