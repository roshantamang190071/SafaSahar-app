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
                    //saveUsernamePassword()
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            MainActivity::class.java
                        )
                    )
                    //showNotification()
                    finish()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Invalid credentials!!!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("Error", ex.toString())

                    Toast.makeText(
                        this@LoginActivity,
                        "Network error!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    }

    private fun invalidForm() {

        AlertDialog.Builder(this)
            .setTitle("Invalid Form!")
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
        if (phoneNumberText.length != 10) {
            return "Invalid phone number"
        }
        if (!phoneNumberText.matches(".*[0-9].*".toRegex())) {
            return "Cannot contain alphabets or special characters!"
        }
        return null

    }

    private fun validPassword(): String? {

        val passwordText = binding.passwordEditText.text.toString()

        if(passwordText == ""){
            return "Required"
        }
        if (passwordText.length < 8) {
            return "Minimum 8 characters required!"
        }
        if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Must contain one uppercase character!"
        }
        if (!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Must contain one lowercase character!"
        }
        if (!passwordText.matches(".*[@#$%&*!].*".toRegex())) {
            return "Must contain one special character(@#$%&*!)!"
        }

        return null
    }
}