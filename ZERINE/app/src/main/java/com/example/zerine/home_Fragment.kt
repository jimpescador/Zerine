package com.example.zerine
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import com.example.zerine.R;

class home_Fragment : Fragment() {

    private lateinit var viewFlipper: ViewFlipper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_, container, false)

        viewFlipper = view.findViewById(R.id.viewflipper)

        val btnLeft: ImageButton = view.findViewById(R.id.leftbtn)
        val btnRight: ImageButton = view.findViewById(R.id.rightbtn)

        btnLeft.setOnClickListener {
            viewFlipper.showPrevious()
        }

        btnRight.setOnClickListener {
            viewFlipper.showNext()
        }

        return view
    }

    // Optional: Handle button clicks in XML with onClick attribute
    fun onButtonClick(view: View) {
        when (view.id) {
            R.id.leftbtn -> viewFlipper.showPrevious()
            R.id.rightbtn -> viewFlipper.showNext()
        }
    }
}
