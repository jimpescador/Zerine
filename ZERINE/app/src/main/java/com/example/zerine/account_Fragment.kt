package com.example.zerine
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class account_Fragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ... (your existing code)
        val view = inflater.inflate(R.layout.fragment_account_, container, false)
        val nameLabel: TextView = view.findViewById(R.id.namelabel)
        val mobileLabel: TextView = view.findViewById(R.id.mobilelabel)
        val nameEdit: EditText = view.findViewById(R.id.EditText_Name)
        val mobileEdit: EditText = view.findViewById(R.id.EditText_Mobile)
        val editBtn: Button = view.findViewById(R.id.btnEdit)
        val applyBtn: Button = view.findViewById(R.id.btnApply)
        val nametxt: TextView = view.findViewById(R.id.NameTXT)
        val mobiletxt: TextView = view.findViewById(R.id.MobileTXT)
        val pgbar: ProgressBar = view.findViewById(R.id.progressBarAcc)
        val accExitBtn: ImageView = view.findViewById(R.id.accExitBtn)


        val currentUser: FirebaseUser? = mAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("info").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val recentName = document.getString("Name")
                        val recentMobile = document.getString("Mobile")

                        // Set the values to your UI elements
                        nameLabel.text = recentName
                        mobileLabel.text = recentMobile

                        // Set the recent values to the EditTexts as well, if needed
                        nameEdit.setText(recentName)
                        mobileEdit.setText(recentMobile)
                    } else {
                        // Document doesn't exist, handle accordingly
                        Toast.makeText(context, "Input Name and Mobile #", Toast.LENGTH_SHORT).show()

                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching document: $e", Toast.LENGTH_SHORT).show()

                }
        } else {
            // No user is signed in, handle accordingly
            Toast.makeText(context, "No user signed in", Toast.LENGTH_SHORT).show()
        }



        accExitBtn.setOnClickListener {

        }

        editBtn.setOnClickListener {
            nameEdit.visibility = View.VISIBLE
            mobileEdit.visibility = View.VISIBLE
            applyBtn.visibility = View.VISIBLE
            nametxt.visibility = View.VISIBLE
            mobiletxt.visibility = View.VISIBLE
            editBtn.visibility = View.GONE
            nameLabel.visibility = View.GONE
            mobileLabel.visibility = View.GONE
        }

        applyBtn.setOnClickListener {
            // Get the updated name and mobile values
            val updatedName = nameEdit.text.toString()
            val updatedMobile = mobileEdit.text.toString()
            pgbar.visibility = View.VISIBLE

            // Get the currently signed-in user's ID
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId2 = currentUser?.uid

            if (userId2 != null) {
                val info = hashMapOf(
                    "Name" to updatedName,
                    "Mobile" to updatedMobile
                )

                firestore.collection("info").document(userId2)
                    .set(info)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
                        pgbar.visibility = View.GONE
                        nameEdit.visibility = View.GONE
                        mobileEdit.visibility = View.GONE
                        applyBtn.visibility = View.GONE
                        nametxt.visibility = View.GONE
                        mobiletxt.visibility = View.GONE
                        editBtn.visibility = View.VISIBLE
                        nameLabel.visibility = View.VISIBLE
                        mobileLabel.visibility = View.VISIBLE
                        nameLabel.text = updatedName
                        mobileLabel.text = updatedMobile
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error updating document: $e", Toast.LENGTH_SHORT).show()
                        pgbar.visibility = View.GONE
                    }
            } else {
                // Handle the case where there is no currently signed-in user
                Toast.makeText(context, "No user signed in", Toast.LENGTH_SHORT).show()
                pgbar.visibility = View.GONE
            }
        }


        return view
    }


}