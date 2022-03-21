package com.example.safasahar.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import api.ServiceBuilder
import com.example.safasahar.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import repository.UserRepository
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumberFocusListener()
        passwordFocusListener()

        binding.loginButton.setOnClickListener {
            submitForm()
        }

        binding.registerTextView.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegistrationActivity::class.java
                )
            )
        }

    }

    private fun submitForm() {
        binding.phoneNumberContainer.helperText = validPhoneNumber()
        binding.passwordContainer.helperText = validPassword()

        val validPhoneNumber = binding.phoneNumberContainer.helperText == null
        val validPassword = binding.passwordContainer.helperText == null

        if (validPhoneNumber && validPassword
        ) {
            login()
        }
        else {
            invalidForm()
        }
    }

    private fun login() {

        val phoneNumber = binding.phoneNumberEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = UserRepository()
                val response = repository.loginUser(phoneNumber, password)

                if (response.success == true) {
                    // Save token
                    ServiceBuilder.token = "Bearer ${response.token}"
                    ServiceBuilder.userId = response.data
                    //Save username and password in shared preferences
                    saveUsernamePassword()
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            UserDashboard::class.java
                        )
                    )
                    //showNotification()
                    finish()
                } else {
                    withContext(Dispatchers.Main) {

                        AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Login failed!")
                        .setMessage("Invalid Credentials!")
                        .setPositiveButton("Ok"){ _, _ ->

                        }.show()

                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("Error", ex.toString())

                    AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Unable to login!")
                        .setMessage("Invalid Credentials!")
                        .setPositiveButton("Ok"){ _, _ ->

                        }.show()
                }
            }

        }

    }

    private fun invalidForm() {

        AlertDialog.Builder(this)
            .setTitle("Invalid Login!")
            .setMessage("Please try again!")
            .setPositiveButton("Back") { _, _ ->
                //do nothing
            }.show()

    }

    private fun passwordFocusListener() {
        binding.passwordContainer.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.passwordContainer.helperText = validPassword()
            }
        }
    }

    private fun phoneNumberFocusListener() {
        binding.phoneNumberContainer.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.phoneNumberContainer.helperText = validPhoneNumber()
            }
        }
    }


    private fun validPhoneNumber(): String? {

        val phoneNumberText = binding.phoneNumberEditText.text.toString()
        if(phoneNumberText == ""){
            return "Required"
        }
        return null

    }

    private fun validPassword(): String? {

        val passwordText = binding.passwordEditText.text.toString()

        if(passwordText == ""){
            return "Required"
        }

        return null
    }


    private fun saveUsernamePassword() {

        val phoneNumber = binding.phoneNumberEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val sharedPref = getSharedPreferences("user", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("phoneNumber", phoneNumber)
        editor.putString("password", password)
        editor.apply()
    }

}