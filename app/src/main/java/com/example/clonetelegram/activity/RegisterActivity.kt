package com.example.clonetelegram.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.clonetelegram.R
import com.example.clonetelegram.UI.fragments.EnterPhoneNumberFragment
import com.example.clonetelegram.databinding.ActivityRegisterBinding
import com.example.clonetelegram.utils.initFirebase
import com.example.clonetelegram.utils.replaceActivity
import com.example.clonetelegram.utils.replaceFragment

class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initFirebase()
    }

    override fun onStart() {
        super.onStart()
        mToolbar = mBinding.registerToolbar
        setSupportActionBar(mToolbar)
        title = getString(R.string.register_title_your_phone)
        replaceFragment(EnterPhoneNumberFragment(),false)


    }
}