package com.example.clonetelegram.UI.fragments

import com.example.clonetelegram.R
import com.example.clonetelegram.database.*
import com.example.clonetelegram.utils.*
import kotlinx.android.synthetic.main.fragment_change_bio.*


class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {


    override fun onResume() {
        super.onResume()
        settings_input_bio.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = settings_input_bio.text.toString()

        setBioToDatabase(newBio)

    }


}