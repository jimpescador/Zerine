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
    private lateinit var auth: FirebaseAuth
    private val SHARED_DOCUMENT_ID = "sharedTriggerValues"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_trigger)

        numberInput1 = findViewById(R.id.numberInput)
        numberInput2 = findViewById(R.id.alertnumberInput)
        val incrementButton = findViewById<Button>(R.id.incrementButton)
        val decrementButton = findViewById<Button>(R.id.decrementButton)

        val aincrementButton = findViewById<Button>(R.id.aincrementButton)
        val adecrementButton = findViewById<Button>(R.id.adecrementButton)

        val saveButton = findViewById<Button>(R.id.saveButton)
        auth = FirebaseAuth.getInstance()

        incrementButton.setOnClickListener { incrementNumber(numberInput1) }
        decrementButton.setOnClickListener { decrementNumber(numberInput1) }

        aincrementButton.setOnClickListener { incrementNumber(numberInput2) }
        adecrementButton.setOnClickListener { decrementNumber(numberInput2) }

        saveButton.setOnClickListener { saveToFirebase() }
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

        if (numberToSave1.isNaN() || numberToSave2.isNaN()) {
            // Handle invalid input (NaN)
            return
        }

        val dataToSave = mapOf("Warning" to numberToSave1, "Alert" to numberToSave2)

        // Access Firestore collection with the shared document
        val sharedDocument = db.collection("TriggerValues").document(SHARED_DOCUMENT_ID)

        sharedDocument
            .set(dataToSave)
            .addOnSuccessListener {
                println("DocumentSnapshot added with ID: ${sharedDocument.id}")

                // Clear the input fields
                numberInput1.text.clear()
                numberInput2.text.clear()

                // Display a toast indicating successful save
                Toast.makeText(this, "Trigger Values saved", Toast.LENGTH_SHORT).show()

                // Update the UI with the saved data
                updateUIWithSavedData(sharedDocument)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
                // Handle the error, e.g., show a message to the user
            }
    }

    private fun updateUIWithSavedData(documentReference: DocumentReference) {
        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val savedNumber1 = documentSnapshot.getDouble("Warning") ?: 0.0
                    val savedNumber2 = documentSnapshot.getDouble("Alert") ?: 0.0

                    // Set the retrieved values to the EditText fields
                    numberInput1.setText(savedNumber1.toString())
                    numberInput2.setText(savedNumber2.toString())

                    // You can also trigger any other UI updates or actions here
                    // For example, you can update TextViews, display a toast, etc.
                } else {
                    numberInput1.setText("106")
                    numberInput2.setText("120")
                    println("Document does not exist")
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching document: $e")
            }
    }








}

