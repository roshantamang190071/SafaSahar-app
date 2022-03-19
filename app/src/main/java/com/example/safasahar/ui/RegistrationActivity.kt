package com.example.safasahar.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.safasahar.R
import com.example.safasahar.databinding.ActivityMainBinding
import com.example.safasahar.databinding.ActivityRegistrationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.UserModel
import repository.UserRepository


//private lateinit var etFullName: EditText
//private lateinit var etAddress: EditText
//private lateinit var etPhoneNumber: EditText
//private lateinit var etPassword: EditText
//private lateinit var etConfirmPassword: EditText
//private lateinit var btnRegister: Button

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fullNameFocusListener()
        phoneNumberFocusListener()
        passwordFocusListener()
        confirmPasswordFocusListener()

        binding.registerButton.setOnClickListener {
            submitForm()
        }
    }

    private fun submitForm() {
        binding.fullNameContainer.helperText = validFullName()
        binding.phoneNumberContainer.helperText = validPhoneNumber()
        binding.passwordContainer.helperText = validPassword()
        binding.confirmPasswordContainer.helperText = validConfirmPassword()

        val validFullName = binding.fullNameContainer.helperText == null
        val validPhoneNumber = binding.phoneNumberContainer.helperText == null
        val validPassword = binding.passwordContainer.helperText == null
        val validConfirmPassword = binding.confirmPasswordContainer.helperText == null

        if (validFullName && validPhoneNumber
            && validPassword && validConfirmPassword
        ) {
            register()
        }
        else {
            invalidForm()
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

    private fun register() {

        val fullName = binding.fullNameEditText.text.toString()
        val phoneNumber = binding.phoneNumberEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        //api
        val user = UserModel(
            fullName = fullName,
            phoneNumber = phoneNumber,
            password = password
        )

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val repository = UserRepository()
                val response = repository.registerUser(user)

                if (response.success == true) {
                    withContext(Dispatchers.Main) {

                        Toast.makeText(
                            this@RegistrationActivity,
                            "Registration successful!",
                            Toast.LENGTH_SHORT
                        ).show()

//                        AlertDialog.Builder(this@RegistrationActivity)
//                            .setTitle("Registration successful!")
//                            .setMessage("You account has been successfully registered!")
//                            .setPositiveButton("Log in") { _, _ ->
//                                //do nothing
//                            }.show()


                        startActivity(
                            Intent(
                                this@RegistrationActivity,
                                LoginActivity::class.java
                            )
                        )
                    }
                    finish()
                }
            } catch (ex: Exception) {
                println(ex)
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@RegistrationActivity)
                        .setTitle("Error!")
                        .setMessage("Something went wrong!")
                        .setPositiveButton("Back") { _, _ ->
                            //do nothing
                        }.show()

                }

            }

        }
    }

    private fun fullNameFocusListener() {
        binding.fullNameContainer.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.fullNameContainer.helperText = validFullName()
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

    private fun passwordFocusListener() {
        binding.passwordContainer.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.passwordContainer.helperText = validPassword()
            }
        }
    }

    private fun confirmPasswordFocusListener() {
        binding.confirmPasswordContainer.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.confirmPasswordContainer.helperText = validConfirmPassword()
            }
        }
    }


    private fun validFullName(): String? {
        val fullNameText = binding.fullNameEditText.text.toString()

        if (!fullNameText.matches("(^[A-Za-z]{3,16})([ ]?)([A-Za-z]{3,16})?([ ]?)?([A-Za-z]{3,16})?([ ]?)?([A-Za-z]{3,16})".toRegex())) {
            return "Invalid Name"
        }
        return null
    }

    private fun validPhoneNumber(): String? {

        val phoneNumberText = binding.phoneNumberEditText.text.toString()
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

    private fun validConfirmPassword(): String? {

        val passwordText = binding.passwordEditText.text.toString()
        val confirmPasswordText = binding.confirmPasswordEditText.text.toString()

        if (passwordText.isEmpty()) {
            return "Required"
        }
        if (passwordText != confirmPasswordText) {
            return "Passwords do not match!"
        }

        return null
    }


}


//        etFullName = findViewById(R.id.etFullName)
//        etAddress = findViewById(R.id.etAddress)
//        etPassword = findViewById(R.id.etPassword)
//        etConfirmPassword = findViewById(R.id.etConfirmPassword)
//        etPhoneNumber = findViewById(R.id.etPhoneNumber)
//        btnRegister = findViewById(R.id.btnRegister)
//
//        btnRegister.setOnClickListener {
//
//            val fullName = etFullName.text.toString()
//            val address = etAddress.text.toString()
//            val phoneNumber = etPhoneNumber.text.toString()
//            val password = etPassword.text.toString()
//            val confirmPassword = etConfirmPassword.text.toString()
//
//            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber) ||
//                TextUtils.isEmpty(address) || TextUtils.isEmpty(password) ||
//                TextUtils.isEmpty(confirmPassword)
//            ) {
//                Toast.makeText(
//                    this@RegistrationActivity,
//                    "Please fill all text fields!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else if (password != confirmPassword) {
//                etPassword.error = "Password does not match!"
//                etPassword.requestFocus()
//                return@setOnClickListener
//            } else {
//
//        }