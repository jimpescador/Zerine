package com.example.zerine

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class settings_Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_, container, false)
        val logout: Button = view.findViewById(R.id.button_LOGOUT)

        logout.setOnClickListener{
            showExitDialog()


        }

        return view
    }
    private fun showExitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to Logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            (activity as? AppCompatActivity)?.finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->

            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



}