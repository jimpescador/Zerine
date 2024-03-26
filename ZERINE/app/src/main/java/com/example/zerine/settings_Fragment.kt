package com.example.zerine


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
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


        logout.setOnClickListener{
            showExitDialog()
        }

        pdfbtn.setOnClickListener{
            generatePdf()
        }
        viewpdf.setOnClickListener{
            displayUserData()
        }

        triggerbtn.setOnClickListener{
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
    fun displayUserData() {
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
                            // Retrieve data for the "info" collection
                            val infoSnapshot = taskList.result?.get(0) as DocumentSnapshot?
                            val name = infoSnapshot?.getString("Name")
                            val mobile = infoSnapshot?.getString("Mobile")

                            // Retrieve data for the "EContacts" collection
                            val eContactsSnapshot = taskList.result?.get(1) as DocumentSnapshot?
                            val eContactsName = eContactsSnapshot?.getString("eContactsName")
                            val eContactsMobile = eContactsSnapshot?.getString("eContactsMobile")

                            // Show the data on the screen using TextViews or any other UI components
                            showDataOnUI(name, mobile, eContactsName, eContactsMobile)

                        } catch (e: Exception) {
                            Log.e("Data Display", "Error: ${e.message}")
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ${taskList.exception?.message}")
                    }
                }
        }
    }



    private fun showDataOnUI(name: String?, mobile: String?, eContactsName: String?, eContactsMobile: String?) {
        // Use TextViews or other UI components to display the data
        val inflater = layoutInflater
        val secondLayout = inflater.inflate(R.layout.fragment_view_records, null)

        // Find TextViews in the second layout
        val nameTextView: TextView = secondLayout.findViewById(R.id.name)
        val mobileTextView: TextView = secondLayout.findViewById(R.id.mobile)
        val eContactsNameTextView: TextView = secondLayout.findViewById(R.id.ename)
        val eContactsMobileTextView: TextView = secondLayout.findViewById(R.id.emobile)

        // Check if data is not null or empty before displaying
        nameTextView.text = "Name: ${name.orEmpty()}"
        mobileTextView.text = "Mobile: ${mobile.orEmpty()}"
        eContactsNameTextView.text = "Emergency Contact Name: ${eContactsName.orEmpty()}"
        eContactsMobileTextView.text = "Emergency Contact Mobile: ${eContactsMobile.orEmpty()}"

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(secondLayout)
        alertDialogBuilder.setPositiveButton("DONE") { dialog, _ ->
            // Handle the OK button click if needed
            dialog.dismiss()
        }

        // Show the AlertDialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    }



