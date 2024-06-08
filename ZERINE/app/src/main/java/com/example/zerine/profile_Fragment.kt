package com.example.zerine

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable

import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.text.InputFilter
import android.text.Spanned
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import java.io.ByteArrayOutputStream


class profile_Fragment : Fragment() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    private val collectionReference: CollectionReference = db.collection("info")

    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_, container, false)
        val sosN: TextView = view.findViewById(R.id.sosname)
        val sosM: TextView = view.findViewById(R.id.sosmobile)
        val nameEdit: EditText = view.findViewById(R.id.Prof_Name)
        val mobileEdit: EditText = view.findViewById(R.id.Prof_Mobile)
        val editBtn: Button = view.findViewById(R.id.prof_btnEdit)
        val applyBtn: Button = view.findViewById(R.id.prof_btnapply)
        val nametxt: TextView = view.findViewById(R.id.name)
        val pgbarprof: ProgressBar = view.findViewById(R.id.progressBarprof)
        val exitbtn: ImageView = view.findViewById(R.id.exitbtn)
        val btnrec: Button = view.findViewById(R.id.button_USER_rec)


        val currentUser: FirebaseUser? = mAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("info").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val recentName = document.getString("Name")

                        // Update TextView
                        nametxt.text = recentName

                        // Update EditText if needed
                        nameEdit.setText(recentName)

                        // Retrieve data from "EContacts" collection

                    } else {
                        // Handle the case when "info" document doesn't exist
                        // This could include clearing/resetting values as needed
                        Toast.makeText(context, "Info document doesn't exist", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching info document: $e", Toast.LENGTH_SHORT)
                        .show()
                }




            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
            } else {
                // Permission is already granted, proceed with your code
                generatePdf()
            }


            val currentUser1: FirebaseUser? = mAuth.currentUser
            if (currentUser1 != null) {
                val userId3 = currentUser1.uid
                db.collection("EContacts").document(userId3)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val sosrecentName = document.getString("eContactsName")
                            val sosrecentMobile = document.getString("eContactsMobile")
                            val sosrecentMobile0 = sosrecentMobile?.substring(3)

                            // Update TextViews
                            sosN.text = sosrecentName
                            sosM.text = sosrecentMobile0

                            // Update EditTexts if needed
                            nameEdit.setText(sosrecentName)
                            mobileEdit.setText(sosrecentMobile0)

                            /*val data = mobileEdit.text.toString()
                            val intent = Intent(requireContext(), ForegroundServices::class.java).apply {
                                putExtra("data_key", data)}
                            requireContext().startService(intent)*/


                        } else {
                            // Handle the case when "EContacts" document doesn't exist
                            // This could include clearing/resetting values as needed
                            Toast.makeText(
                                context,
                                "EContacts document doesn't exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Error fetching EContacts document: $e",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                editBtn.setOnClickListener {
                    nameEdit.visibility = View.VISIBLE
                    mobileEdit.visibility = View.VISIBLE

                    applyBtn.visibility = View.VISIBLE
                    editBtn.visibility = View.GONE


                }

                btnrec.setOnClickListener {
                    generatePdf()
                }

                exitbtn.setOnClickListener {
                    showExitDialog()
                }

                applyBtn.setOnClickListener {
                    // Get the updated name and mobile values

                    val updatedName = nameEdit.text.toString()
                    var updatedMobile = mobileEdit.text.toString()


                    // Check for null or empty values
                    if (updatedName.isEmpty() || updatedMobile.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Name and Mobile cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    pgbarprof.visibility = View.VISIBLE

                    // Update the Firebase Firestore with the new information
                    // Assuming you have a user ID to identify the document

                    val currentUser2 = FirebaseAuth.getInstance().currentUser
                    val userId2 = currentUser2?.uid

                    if (userId2 != null) {
                        val eContacts = hashMapOf(
                            "eContactsName" to updatedName,
                            "eContactsMobile" to updatedMobile
                        )

                        db.collection("EContacts").document(userId2)
                            .set(eContacts)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT)
                                    .show()
                                nameEdit.visibility = View.GONE
                                mobileEdit.visibility = View.GONE
                                applyBtn.visibility = View.GONE
                                pgbarprof.visibility = View.GONE

                                editBtn.visibility = View.VISIBLE
                                sosN.text = updatedName
                                sosM.text = updatedMobile

                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Error updating document: $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                                pgbarprof.visibility = View.GONE
                            }
                    }
                }
            }

        }
        return view
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to exit?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Communicate with the hosting activity to finish
            requireActivity().finishAffinity()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            // User clicked "No," do nothing and dismiss the dialog
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    fun generatePdf() {
        // Get the current user ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            // Fetch data from the "info" collection for the current user
            val infoDocumentReference = db.collection("info").document(userId)
            val eContactsDocumentReference = db.collection("EContacts").document(userId)
            val fallDetectionCollectionReference = db.collection("fallDetection")
            val seizureRecordsCollectionReference = db.collection("SeizureRecords")
            val bpmCollectionReference = db.collection("BPM")

            // Use Tasks.whenAllSuccess to combine results from all collections
            Tasks.whenAllSuccess<DocumentSnapshot>(
                infoDocumentReference.get(),
                eContactsDocumentReference.get()
            ).addOnCompleteListener { taskList ->
                if (taskList.isSuccessful) {
                    try {
                        // Create a PDF document
                        val document = Document()

                        // Use getExternalFilesDir to get a directory where your app can write
                        val directory = requireContext().getExternalFilesDir(null)

                        // Add a timestamp to the file name to make it unique
                        val timestamp = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(Date())
                        val fileName = "Zerine_Record_$timestamp.pdf"

                        val filePath = File(directory, fileName)

                        // Create the parent directories if they don't exist
                        filePath.parentFile?.mkdirs()

                        PdfWriter.getInstance(document, FileOutputStream(filePath))

                        document.open()

                        // Add title "ZERINE"
                        val titleFont = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD)
                        val title = Paragraph("ZERINE", titleFont).apply {
                            alignment = Element.ALIGN_CENTER
                        }
                        document.add(title)

                        // Add logo
                        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo)
                        if (drawable != null) {
                            val bitmap = (drawable as BitmapDrawable).bitmap
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            val image = Image.getInstance(stream.toByteArray())
                            image.scaleToFit(100f, 100f) // Adjust the size as needed
                            image.alignment = Image.ALIGN_CENTER
                            document.add(image)
                        }

                        val ViewFont = Font(Font.FontFamily.HELVETICA, 15f, Font.BOLD)
                        val Font = Paragraph("Records", ViewFont).apply {
                            alignment = Element.ALIGN_CENTER
                            spacingAfter = 20f
                        }
                        document.add(Font)

                        // Retrieve data for the "info" collection
                        val infoSnapshot = taskList.result?.get(0) as DocumentSnapshot?
                        val name = infoSnapshot?.getString("Name")
                        val mobile = infoSnapshot?.getString("Mobile")

                        // Add "Name" to the PDF
                        document.add(Paragraph("Name: $name").apply {
                            alignment = Element.ALIGN_CENTER // Align text to center
                            spacingAfter = 10f // Add space after the paragraph
                        })

                        // Add "Mobile" to the PDF
                        document.add(Paragraph("Mobile: $mobile").apply {
                            alignment = Element.ALIGN_CENTER // Align text to center
                            spacingAfter = 10f // Add space after the paragraph
                        })

                        // Retrieve data for the "EContacts" collection
                        val eContactsSnapshot = taskList.result?.get(1) as DocumentSnapshot?
                        val eContactsName = eContactsSnapshot?.getString("eContactsName")
                        val eContactsMobile = eContactsSnapshot?.getString("eContactsMobile")

                        // Add "Emergency Contact Name" to the PDF
                        document.add(Paragraph("Emergency Contact Name: $eContactsName").apply {
                            alignment = Element.ALIGN_CENTER // Align text to center
                            spacingAfter = 10f // Add space after the paragraph
                        })

                        // Add "Emergency Contact Mobile" to the PDF
                        document.add(Paragraph("Emergency Contact Mobile: $eContactsMobile").apply {
                            alignment = Element.ALIGN_CENTER // Align text to center
                            spacingAfter = 10f // Add space after the paragraph
                        })

                        // Retrieve the latest AvgBPM from the "BPM" collection
                        bpmCollectionReference.limit(1).get().addOnSuccessListener { bpmDocuments ->
                            val bpmDocument = bpmDocuments.documents.firstOrNull()
                            val avgBpm = bpmDocument?.getDouble("AvgBPM")

                            val formattedAvgBpm = String.format("%.2f", avgBpm)

                            // Add AvgBPM data to the PDF with space and centered alignment
                            document.add(Paragraph("Average BPM: $formattedAvgBpm").apply {
                                alignment = Element.ALIGN_CENTER // Align text to center
                                spacingBefore = 10f
                                spacingAfter = 10f // Add space after the paragraph
                            })


                            // Retrieve data for the "fallDetection" collection
                            fallDetectionCollectionReference.get()
                                .addOnSuccessListener { fallDocuments ->
                                    val fallDataList = mutableListOf<String>()

                                    // Process each fall detection document
                                    for (fallDocument in fallDocuments) {
                                        try {
                                            // Retrieve timestamp
                                            val stampFall =
                                                fallDocument.getTimestamp("timestamp")?.toDate()
                                                    ?.toString()

                                            // Retrieve fall detection status (boolean)
                                            val fallDetected =
                                                fallDocument.getBoolean("fallDetected")
                                            val fallDetectedText =
                                                if (fallDetected == true) "True" else "False"

                                            // Add fall data to the list
                                            fallDataList.add("Fall Detected: $fallDetectedText\nTimestamp: $stampFall")
                                        } catch (e: Exception) {
                                            Log.e(
                                                "PDF Generator",
                                                "Error processing fall detection data: ${e.message}"
                                            )
                                        }
                                    }

                                    // Add Fall Detection data to the PDF with space and centered alignment
                                    document.add(Paragraph("Fall Detection Records:").apply {
                                        alignment = Element.ALIGN_CENTER // Align text to center
                                        spacingAfter = 10f // Add space after the paragraph

                                    })

                                    fallDataList.forEach { fallData ->
                                        document.add(Paragraph(fallData).apply {
                                            alignment = Element.ALIGN_CENTER // Align text to center
                                            spacingBefore = 5f // Add space before each line of data
                                        })
                                    }

                                    // Retrieve data for the "SeizureRecords" collection
                                    seizureRecordsCollectionReference.get()
                                        .addOnSuccessListener { seizureDocuments ->
                                            val seizureDataList = mutableListOf<String>()

                                            // Process each seizure document
                                            for (seizureDocument in seizureDocuments) {
                                                try {
                                                    // Retrieve timestamp
                                                    val stampSeizure =
                                                        seizureDocument.getTimestamp("timestamp")
                                                            ?.toDate()?.toString()

                                                    // Add seizure data to the list
                                                    seizureDataList.add("Timestamp: $stampSeizure")
                                                } catch (e: Exception) {
                                                    Log.e(
                                                        "PDF Generator",
                                                        "Error processing seizure data: ${e.message}"
                                                    )
                                                }
                                            }

                                            // Add Seizure Records data to the PDF with space and centered alignment
                                            document.add(Paragraph("Seizure Records:").apply {
                                                alignment =
                                                    Element.ALIGN_CENTER // Align text to center
                                                spacingBefore = 10f
                                                spacingAfter = 10f // Add space after the paragraph
                                            })

                                            seizureDataList.forEach { seizureData ->
                                                document.add(Paragraph(seizureData).apply {
                                                    alignment =
                                                        Element.ALIGN_CENTER // Align text to center
                                                    spacingBefore =
                                                        5f // Add space before each line of data
                                                })
                                            }





                                            document.close()

                                            // Display the generated PDF using an Intent
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            val uri = FileProvider.getUriForFile(
                                                requireContext(),
                                                "${requireContext().packageName}.provider",
                                                filePath
                                            )
                                            intent.setDataAndType(uri, "application/pdf")
                                            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                                            startActivity(intent)
                                        }
                                }
                        }
                    } catch (e: Exception) {
                        Log.e("PDF Generator", "Error: ${e.message}")
                    }
                } else {
                    Log.e("Firestore", "Error getting documents: ${taskList.exception?.message}")
                }
            }
        }
    }
}










