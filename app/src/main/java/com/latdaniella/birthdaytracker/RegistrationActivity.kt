package com.latdaniella.birthdaytracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.latdaniella.birthdaytracker.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val TAG = "RegistrationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // access any values that were sent to us from the intent that launched this activity
        val username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME)
        val password = intent.getStringExtra(LoginActivity.EXTRA_PASSWORD)
        Toast.makeText(this, "user:$username pwd $password", Toast.LENGTH_SHORT).show()

        binding.buttonRegistrationRegister.setOnClickListener {
            // TODO: verify that the information they entered is valid

            // in a real app, we'd talk to a database here and save the new user

            // return to the Login Screen and send back the user & pass to prefill
            if(!RegistrationUtil.validateName(
                    binding.editTextRegistrationName.text.toString())) {
                Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show()
            }

            else {
                registerUser()
            }

        }
    }
    private fun registerUser(){
        val username = binding.editTextRegistrationUsername.text.toString()
        val password = binding.editTextRegistrationPassword.text.toString()
        val email = binding.editTextRegistrationEmail.text.toString()
        val name = binding.editTextRegistrationName.text.toString()

        val user = BackendlessUser()
        user.setProperty("email", email)
        user.password = password
        user.setProperty("name", name)
        user.setProperty("username", username)

        Backendless.UserService.register(user, object : AsyncCallback<BackendlessUser?> {
            override fun handleResponse(registeredUser: BackendlessUser?) {
                returnToLogin(username, password)
                Toast.makeText(this@RegistrationActivity,
                    "${registeredUser?.getProperty("username")} has been registered", Toast.LENGTH_SHORT).show()
            }

            override fun handleFault(fault: BackendlessFault) {
                Toast.makeText(this@RegistrationActivity, "Error: ${fault.getCode()}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun returnToLogin(username: String, password: String) {
        var returnToLoginIntent = Intent().apply {
            putExtra(LoginActivity.EXTRA_USERNAME, binding.editTextRegistrationUsername.text.toString())
            putExtra(LoginActivity.EXTRA_PASSWORD, binding.editTextRegistrationPassword.text.toString())
        }
        setResult(Activity.RESULT_OK, returnToLoginIntent)
        finish()
    }
}










