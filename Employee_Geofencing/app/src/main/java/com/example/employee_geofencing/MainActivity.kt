package com.example.employee_geofencing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.employee_geofencing.ui.theme.Employee_GeofencingTheme
import android.Manifest
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.fuelflex.location.LocationManager
import com.example.fuelflex.network.LoginRequest
import com.example.fuelflex.network.RetrofitClient
import com.example.fuelflex.network.RegisterRequest
import android.widget.Toast
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : ComponentActivity() {
    private val locationPermissionRequest = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerTextView =
            findViewById<TextView>(R.id.registerTextView)
        val checkInButton =
            findViewById<Button>(R.id.checkInButton)

        val checkOutButton =
            findViewById<Button>(R.id.checkOutButton)
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {

            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter username and password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {

                    val api = RetrofitClient.create(this@MainActivity)

                    val response = api.login(
                        LoginRequest(
                            username = username,
                            password = password
                        )
                    )

                    getSharedPreferences(
                        "auth",
                        MODE_PRIVATE
                    )
                        .edit()
                        .putString("access_token", response.access)
                        .putString("refresh_token", response.refresh)
                        .apply()

                    Log.d(
                        "LOGIN_TEST",
                        "LOGIN SUCCESS"
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Login successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (hasLocationPermission()) {
                        getLocation()
                    }

                } catch (e: retrofit2.HttpException) {

                    val errorBody = e.response()
                        ?.errorBody()
                        ?.string()

                    Log.e(
                        "LOGIN_TEST",
                        "HTTP ${e.code()} ERROR: $errorBody"
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Invalid username or password",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {

                    Log.e(
                        "LOGIN_TEST",
                        "LOGIN FAILED",
                        e
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Network error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        checkInButton.setOnClickListener {

            lifecycleScope.launch {

                try {

                    val api =
                        RetrofitClient.create(this@MainActivity)

                    val response = api.checkIn()

                    Toast.makeText(
                        this@MainActivity,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d(
                        "ATTENDANCE",
                        "CHECK-IN: ${response.message}"
                    )

                } catch (e: retrofit2.HttpException) {

                    val errorBody =
                        e.response()?.errorBody()?.string()

                    Log.e(
                        "ATTENDANCE",
                        "CHECK-IN HTTP ${e.code()}: $errorBody"
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Check-in failed",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {

                    Log.e(
                        "ATTENDANCE",
                        "CHECK-IN FAILED",
                        e
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Network error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        checkOutButton.setOnClickListener {

            lifecycleScope.launch {

                try {

                    val api =
                        RetrofitClient.create(this@MainActivity)

                    val response = api.checkOut()

                    Toast.makeText(
                        this@MainActivity,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d(
                        "ATTENDANCE",
                        "CHECK-OUT: ${response.message}"
                    )

                } catch (e: retrofit2.HttpException) {

                    val errorBody =
                        e.response()?.errorBody()?.string()

                    Log.e(
                        "ATTENDANCE",
                        "CHECK-OUT HTTP ${e.code()}: $errorBody"
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Check-out failed",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {

                    Log.e(
                        "ATTENDANCE",
                        "CHECK-OUT FAILED",
                        e
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Network error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
//        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
//        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
//        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
//        val employeeIdEditText = findViewById<EditText>(R.id.employeeIdEditText)

//        val registerButton = findViewById<Button>(R.id.registerButton)
//        registerButton.setOnClickListener {
//
//            val username = usernameEditText.text.toString().trim()
//            val password = passwordEditText.text.toString()
//            val phone = phoneEditText.text.toString().trim()
//            val employeeId = employeeIdEditText.text.toString().trim()
//
//            if (
//                username.isEmpty() ||
//                password.isEmpty() ||
//                phone.isEmpty() ||
//                employeeId.isEmpty()
//            ) {
//                Toast.makeText(
//                    this,
//                    "Please fill all fields",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                return@setOnClickListener
//            }
//
//            lifecycleScope.launch {
//
//                try {
//
//                    val api = RetrofitClient.create(this@MainActivity)
//
//                    val response = api.register(
//                        RegisterRequest(
//                            username = username,
//                            password = password,
//                            phone = phone,
//                            employee_id = employeeId
//                        )
//                    )
//
//                    Log.d(
//                        "REGISTER_TEST",
//                        "REGISTER SUCCESS: ${response.message}"
//                    )
//
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Registration successful",
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//                } catch (e: retrofit2.HttpException) {
//
//                    val errorBody = e.response()
//                        ?.errorBody()
//                        ?.string()
//
//                    Log.e(
//                        "REGISTER_TEST",
//                        "HTTP ${e.code()} ERROR: $errorBody"
//                    )
//
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Registration failed",
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//                } catch (e: Exception) {
//
//                    Log.e(
//                        "REGISTER_TEST",
//                        "REGISTER FAILED",
//                        e
//                    )
//
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Network error",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
        //login()
        if (!hasLocationPermission()) {
            requestLocationPermission()
        }


    }
    private fun requestLocationPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            locationPermissionRequest
        )
    }
    private fun hasLocationPermission(): Boolean {

        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun getLocation() {
        val locationManager = LocationManager(this)

        lifecycleScope.launch {
            val location = locationManager.getCurrentLocation()

            if (location != null) {

                Log.d(
                    "GPS_TEST",
                    "LAT=${location.latitude}, " +
                            "LON=${location.longitude}, " +
                            "ACCURACY=${location.accuracy}"
                )

                try {
                    val api = RetrofitClient.create(this@MainActivity)

                    val response = api.sendLocation(
                        com.example.fuelflex.location.LocationRequest(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy
                        )
                    )

                    Log.d(
                        "LOCATION_API",
                        "STATUS=${response.status}, " +
                                "DISTANCE=${response.distance_meters}m, " +
                                "RADIUS=${response.geofence_radius_meters}m"
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Location: ${response.status}",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    Log.e("LOCATION_API", "FAILED TO SEND LOCATION", e)
                }

            } else {
                Log.d("GPS_TEST", "LOCATION IS NULL")
            }
        }
    }
    private fun login() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@MainActivity)

                val response = api.login(
                    LoginRequest(
                        username = "darshan",
                        password = "employee@1"
                    )
                )

                getSharedPreferences("auth", MODE_PRIVATE)
                    .edit()
                    .putString("access_token", response.access)
                    .putString("refresh_token", response.refresh)
                    .apply()

                Log.d("LOGIN_TEST", "LOGIN SUCCESS")
                if (hasLocationPermission()) {
                    getLocation()
                }
                Toast.makeText(
                    this@MainActivity,
                    "Login successful",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Log.e("LOGIN_TEST", "LOGIN FAILED", e)

                Toast.makeText(
                    this@MainActivity,
                    "Login failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Employee_GeofencingTheme {
        Greeting("Android")
    }
}