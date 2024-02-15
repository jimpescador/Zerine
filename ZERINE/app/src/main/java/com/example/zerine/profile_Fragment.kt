package com.example.zerine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore


class profile_Fragment : Fragment() {
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_, container, false)

        val sosnameLabel: TextView = view.findViewById(R.id.sosname)
        val sosmobileLabel: TextView = view.findViewById(R.id.sosmobile)
        val nameEdit: EditText = view.findViewById(R.id.Prof_Name)
        val mobileEdit: EditText = view.findViewById(R.id.Prof_Mobile)
        val editBtn: Button = view.findViewById(R.id.prof_btnEdit)
        val applyBtn: Button = view.findViewById(R.id.prof_btnapply)
        val nametxt: TextView = view.findViewById(R.id.name)



        editBtn.setOnClickListener {
            nameEdit.visibility = View.VISIBLE
            mobileEdit.visibility = View.VISIBLE
            applyBtn.visibility = View.VISIBLE
            nametxt.visibility = View.VISIBLE
            editBtn.visibility = View.GONE
            

        }

        applyBtn.setOnClickListener {
            // Get the updated name and mobile values
            val updatedName = nameEdit.text.toString()
            val updatedMobile = mobileEdit.text.toString()

            // Update the Firebase Firestore with the new information
            // Assuming you have a user ID to identify the document
            val userId = "your_user_id"

            val info = hashMapOf(
                "Name" to updatedName,
                "Mobile" to updatedMobile
            )

            firestore.collection("info").document(userId)
                .set(info)
                .addOnSuccessListener {
                    Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
                    nameEdit.visibility = View.GONE
                    mobileEdit.visibility = View.GONE
                    applyBtn.visibility = View.GONE
                    nametxt.visibility = View.GONE

                    editBtn.visibility = View.VISIBLE



                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error updating document: $e", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }
}





