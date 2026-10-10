package com.example.employee_geofencing

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fuelflex.network.RegisterRequest
import com.example.fuelflex.network.RetrofitClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val usernameEditText =
            findViewById<EditText>(R.id.usernameEditText)

        val passwordEditText =
            findViewById<EditText>(R.id.passwordEditText)

        val phoneEditText =
            findViewById<EditText>(R.id.phoneEditText)

        val employeeIdEditText =
            findViewById<EditText>(R.id.employeeIdEditText)

        val registerButton =
            findViewById<Button>(R.id.registerButton)

        val loginTextView =
            findViewById<TextView>(R.id.loginTextView)

        registerButton.setOnClickListener {

            val username =
                usernameEditText.text.toString().trim()

            val password =
                passwordEditText.text.toString()

            val phone =
                phoneEditText.text.toString().trim()

            val employeeId =
                employeeIdEditText.text.toString().trim()

            if (
                username.isEmpty() ||
                password.isEmpty() ||
                phone.isEmpty() ||
                employeeId.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            lifecycleScope.launch {

                try {

                    val api =
                        RetrofitClient.create(this@RegisterActivity)

                    val response = api.register(
                        RegisterRequest(
                            username = username,
                            password = password,
                            phone = phone,
                            employee_id = employeeId
                        )
                    )

                    Log.d(
                        "REGISTER_TEST",
                        "REGISTER SUCCESS: ${response.message}"
                    )

                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } catch (e: retrofit2.HttpException) {

                    val errorBody =
                        e.response()
                            ?.errorBody()
                            ?.string()

                    Log.e(
                        "REGISTER_TEST",
                        "HTTP ${e.code()} ERROR: $errorBody"
                    )

                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration failed",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {

                    Log.e(
                        "REGISTER_TEST",
                        "REGISTER FAILED",
                        e
                    )

                    Toast.makeText(
                        this@RegisterActivity,
                        "Network error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        loginTextView.setOnClickListener {
            finish()
        }
    }
}