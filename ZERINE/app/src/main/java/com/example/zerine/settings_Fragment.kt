package com.example.zerine


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class settings_Fragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_, container, false)
        val logout: Button = view.findViewById(R.id.button_LOGOUT)
        val pdfbtn: Button = view.findViewById(R.id.button_ToPDF)
        val triggerbtn: Button = view.findViewById(R.id.button_EditBPM)
        val viewpdf: Button = view.findViewById(R.id.button_ViewRecords)


        logout.setOnClickListener {
            showExitDialog()
        }

        pdfbtn.setOnClickListener {
            generatePdf()
        }
        viewpdf.setOnClickListener {
            displayUserData()
        }

        triggerbtn.setOnClickListener {
            val intent = Intent(requireContext(), TriggerEdit::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to Logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            mAuth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            (activity as? AppCompatActivity)?.finish()
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->

            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun generatePdf() {
        // Get the current user ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            // Fetch data from the "info" collection for the current user
            val infoDocumentReference = db.collection("info").document(userId)
            val eContactsDocumentReference = db.collection("EContacts").document(userId)
            val bpmDocumentReference = db.collection("BPM").document(userId)
            val fallDetectionCollectionReference = db.collection("fallDetection")
            val seizureRecordsCollectionReference = db.collection("SeizureRecords")

            // Use Tasks.whenAllSuccess to combine results from all collections
            Tasks.whenAllSuccess<DocumentSnapshot>(
                infoDocumentReference.get(),
                eContactsDocumentReference.get(),
                bpmDocumentReference.get()
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

                        // Retrieve data for the "BPM" collection
                        val bpmSnapshot = taskList.result?.get(2) as DocumentSnapshot?
                        val avgBPM = bpmSnapshot?.getDouble("AvgBPM")

                        // Add "Average BPM" to the PDF
                        val bpmText = if (avgBPM != null) avgBPM.toString() else "N/A"
                        document.add(Paragraph("Average BPM: $bpmText").apply {
                            alignment = Element.ALIGN_CENTER // Align text to center
                            spacingAfter = 10f // Add space after the paragraph
                        })

                        // Retrieve data for the "fallDetection" collection
                        fallDetectionCollectionReference.get().addOnSuccessListener { fallDocuments ->
                            val fallDataList = mutableListOf<String>()

                            // Process each fall detection document
                            for (fallDocument in fallDocuments) {
                                try {
                                    // Retrieve timestamp
                                    val stampFall =
                                        fallDocument.getTimestamp("timestamp")?.toDate()?.toString()

                                    // Retrieve fall detection status (boolean)
                                    val fallDetected = fallDocument.getBoolean("fall")
                                    val fallDetectedText = if (fallDetected == true) "Yes" else "No"

                                    // Add fall data to the list
                                    fallDataList.add(
                                        "Fall Detected: $fallDetectedText\nTimestamp: $stampFall"
                                    )

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
                            seizureRecordsCollectionReference.get().addOnSuccessListener { seizureDocuments ->
                                val seizureDataList = mutableListOf<String>()

                                // Process each seizure document
                                for (seizureDocument in seizureDocuments) {
                                    try {
                                        // Retrieve timestamp
                                        val stampSeizure =
                                            seizureDocument.getTimestamp("timestamp")?.toDate()?.toString()

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
                                    alignment = Element.ALIGN_CENTER // Align text to center
                                    spacingAfter = 10f // Add space after the paragraph
                                })

                                seizureDataList.forEach { seizureData ->
                                    document.add(Paragraph(seizureData).apply {
                                        alignment = Element.ALIGN_CENTER // Align text to center
                                        spacingBefore = 5f // Add space before each line of data
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
                        }.addOnFailureListener { exception ->
                            Log.e("Firestore", "Error getting documents: $exception")
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


    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }











    data class FallData(
        val fall: String?, // Fall detection status (e.g., "true" or "false")
        val stampFall: String? // Timestamp of the fall detection
    )

    data class SeizureData(
        val stampSeizure: String? // Timestamp of the fall detection
    )

    fun displayUserData() {
        // Get the current user ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            // Fetch data from the "info" and "EContacts" collections for the current user
            val infoDocumentReference = db.collection("info").document(userId)
            val eContactsDocumentReference = db.collection("EContacts").document(userId)

            // Fetch the most recent data from the "BPM" collection
            db.collection("BPM")
                .limit(1)
                .get()
                .addOnSuccessListener { bpmDocuments ->
                    val avgBPM = if (!bpmDocuments.isEmpty) {
                        val bpm = bpmDocuments.documents[0].getDouble("AvgBPM")
                        String.format("%.1f", bpm)
                    } else {
                        null
                    }
                    Log.d("Firestore", "AvgBPM retrieved successfully: $avgBPM")
                    // Continue processing the retrieved data here


                    // Fetch all data from the "fallDetection" collection
                    db.collection("fallDetection")
                        .get()
                        .addOnSuccessListener { fallDocuments ->
                            val fallDataList = mutableListOf<FallData>()

                            // Process each fall detection document
                            for (fallDocument in fallDocuments) {
                                try {
                                    // Retrieve timestamp
                                    val stampFall =
                                        fallDocument.getTimestamp("timestamp")?.toDate()?.toString()

                                    // Add fall data to the list
                                    fallDataList.add(
                                        FallData(
                                            null,
                                            stampFall
                                        )
                                    ) // Fall data without fallDetected field

                                } catch (e: Exception) {
                                    Log.e(
                                        "Data Display",
                                        "Error processing fall detection data: ${e.message}"
                                    )
                                }
                            }

                            db.collection("SeizureRecords")
                                .get()
                                .addOnSuccessListener { seizureDocuments ->
                                    val seizureDataList = mutableListOf<SeizureData>()

                                    // Process each fall detection document
                                    for (seizureDocuments in seizureDocuments) {
                                        try {
                                            // Retrieve timestamp
                                            val stampSeizure =
                                                seizureDocuments.getTimestamp("timestamp")?.toDate()
                                                    ?.toString()

                                            // Add fall data to the list
                                            seizureDataList.add(SeizureData(stampSeizure))

                                        } catch (e: Exception) {
                                            Log.e(
                                                "Data Display",
                                                "Error processing seizure data: ${e.message}"
                                            )
                                        }
                                    }

                                    // Retrieve data for the "info" collection
                                    infoDocumentReference.get()
                                        .addOnSuccessListener { infoSnapshot ->
                                            val name = infoSnapshot.getString("Name")
                                            val mobile = infoSnapshot.getString("Mobile")

                                            // Retrieve data for the "EContacts" collection
                                            eContactsDocumentReference.get()
                                                .addOnSuccessListener { eContactsSnapshot ->
                                                    val eContactsName =
                                                        eContactsSnapshot.getString("eContactsName")
                                                    val eContactsMobile =
                                                        eContactsSnapshot.getString("eContactsMobile")

                                                    // Show the data on the screen using TextViews or any other UI components
                                                    showDataOnUI(
                                                        name,
                                                        mobile,
                                                        eContactsName,
                                                        eContactsMobile,
                                                        avgBPM,
                                                        fallDataList,
                                                        seizureDataList
                                                    )
                                                }.addOnFailureListener { e ->
                                                    Log.e(
                                                        "Firestore",
                                                        "Error getting EContacts document: ${e.message}"
                                                    )
                                                    // Handle failure to retrieve EContacts document
                                                }
                                        }.addOnFailureListener { e ->
                                            Log.e(
                                                "Firestore",
                                                "Error getting info document: ${e.message}"
                                            )
                                            // Handle failure to retrieve info document
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(
                                        "Firestore",
                                        "Error getting fall detection documents: ",
                                        exception
                                    )
                                    // Handle failure to retrieve fall detection documents
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore", "Error getting BPM documents: ", exception)
                            // Handle failure to retrieve BPM documents
                        }
                }
        }
    }




        private fun showDataOnUI(
            name: String?,
            mobile: String?,
            eContactsName: String?,
            eContactsMobile: String?,
            avgBPM: String?,
            fallDataList: List<FallData>,
            seizureDataList: List<SeizureData>
        ) {
            // Use TextViews or other UI components to display the data
            val inflater = layoutInflater
            val secondLayout = inflater.inflate(R.layout.fragment_view_records, null)

            // Find TextViews in the second layout
            val nameTextView: TextView = secondLayout.findViewById(R.id.name)
            val mobileTextView: TextView = secondLayout.findViewById(R.id.mobile)
            val eContactsNameTextView: TextView = secondLayout.findViewById(R.id.ename)
            val eContactsMobileTextView: TextView = secondLayout.findViewById(R.id.emobile)
            val bpmMobileTextView: TextView = secondLayout.findViewById(R.id.avgBPM3)
            val fallDataListView: ListView = secondLayout.findViewById(R.id.fallDataList)
            val seizureDataListView: ListView = secondLayout.findViewById(R.id.seizureDataList)


            // Check if data is not null or empty before displaying
            nameTextView.text = "Name: ${name.orEmpty()}"
            mobileTextView.text = "Mobile: ${mobile.orEmpty()}"
            eContactsNameTextView.text = "Emergency Contact Name: ${eContactsName.orEmpty()}"
            eContactsMobileTextView.text = "Emergency Contact Mobile: ${eContactsMobile.orEmpty()}"
            bpmMobileTextView.text = "${avgBPM.orEmpty()} BPM"

            // Display fall data list
            val adapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_list_item_1,  // Layout file for each item (built-in layout)
                fallDataList.mapIndexed { index, fallData ->
                    "${index + 1}:\nFall Detected: ${fallData.fall}\nTimestamp: ${fallData.stampFall}\n"
                }
            )
            fallDataListView.adapter = adapter

            val adapter1 = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_list_item_1,  // Layout file for each item (built-in layout)
                seizureDataList.mapIndexed { index, seizureData ->
                    "${index + 1}:\nTimestamp: ${seizureData.stampSeizure}\n"
                }
            )
            seizureDataListView.adapter = adapter1

            // Create and show the AlertDialog
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setView(secondLayout)
            alertDialogBuilder.setPositiveButton("DONE") { dialog, _ ->
                // Handle the OK button click if needed
                dialog.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }


    }





