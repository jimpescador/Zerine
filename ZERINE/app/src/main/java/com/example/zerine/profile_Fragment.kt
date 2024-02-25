package com.example.zerine

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.FileOutputStream
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import android.Manifest
import android.content.Intent
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

                            // Update TextViews
                            sosN.text = sosrecentName
                            sosM.text = sosrecentMobile

                            // Update EditTexts if needed
                            nameEdit.setText(sosrecentName)
                            mobileEdit.setText(sosrecentMobile)
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
                    val updatedMobile = mobileEdit.text.toString()

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

            // Use Tasks.whenAllSuccess to combine results from both collections
            Tasks.whenAllSuccess<DocumentSnapshot>(infoDocumentReference.get(), eContactsDocumentReference.get())
                .addOnCompleteListener { taskList ->
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

                            // Retrieve data for the "info" collection
                            val infoSnapshot = taskList.result?.get(0) as DocumentSnapshot?
                            val name = infoSnapshot?.getString("Name")
                            val mobile = infoSnapshot?.getString("Mobile")

                            // Add "Name" to the PDF
                            document.add(Paragraph("Name: $name"))

                            // Add "Mobile" to the next line in the PDF
                            document.add(Paragraph("Mobile: $mobile"))

                            // Retrieve data for the "EContacts" collection
                            val eContactsSnapshot = taskList.result?.get(1) as DocumentSnapshot?
                            val eContactsName = eContactsSnapshot?.getString("eContactsName")
                            val eContactsMobile = eContactsSnapshot?.getString("eContactsMobile")

                            // Add "Emergency Contact Name" to the PDF
                            document.add(Paragraph("Emergency Contact Name: $eContactsName"))

                            // Add "Emergency Contact Mobile" to the next line in the PDF
                            document.add(Paragraph("Emergency Contact Mobile: $eContactsMobile"))

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

                        } catch (e: Exception) {
                            Log.e("PDF Generator", "Error: ${e.message}")
                        }
                    } else {
                        Log.e(
                            "Firestore",
                            "Error getting documents: ${taskList.exception?.message}"
                        )
                    }
                }
        }

    }
}










