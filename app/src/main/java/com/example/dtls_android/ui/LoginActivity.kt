package com.example.dtls_android.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.dtls_android.R
import com.example.dtls_android.ViewModel.LoginActivityViewModel
import com.example.dtls_android.account.AccountManager
import com.example.dtls_android.service.response.Account
import com.example.dtls_android.session.AccountPref
import com.example.dtls_android.session.LoginPref

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button
    private lateinit var account: AccountPref
    private lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        account = AccountPref(this)
        if (account.isLoggedIn()) {
            redirectToDash()
        }
        usernameField = findViewById(R.id.usernameLoginField)
        passwordField = findViewById(R.id.passwordLoginField)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignup = findViewById(R.id.btnSignup)
        viewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        btnLogin.setOnClickListener{
            login(usernameField.text.toString().trim(), passwordField.text.toString().trim())
        }
        btnSignup.setOnClickListener{
            register()
        }
        loginObservable()
    }

    override fun onBackPressed() {
        exit()
    }

    private fun loginObservable() {
        viewModel.getLoginAccountDataObservable().observe(this, {
            if (it == null) {
                Toast.makeText(this@LoginActivity, "Unable to login.", Toast.LENGTH_LONG).show()
            } else {
                account.saveTokenData(usernameField.text.toString(), it.expiry, it.token)
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                redirectToDash()
            }
        })
    }

    private fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            promptError("Please enter your username and password.")
        } else {
            viewModel.login(Account(username, password))
        }
    }

    private fun register() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun exit() {
        AlertDialog.Builder(this)
            .setTitle("Exit")
            .setIcon(R.drawable.ic_signout)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") {
                dialog,_->
                this@LoginActivity.finish()
                dialog.dismiss()
            }
            .setNegativeButton("No") {
                dialog,_->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun redirectToDash() {
        val intent = Intent(applicationContext, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun promptError(str: String) {
        usernameField.setText("")
        passwordField.setText("")
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }
}