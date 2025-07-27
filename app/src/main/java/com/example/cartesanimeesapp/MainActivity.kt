package com.example.cartesanimeesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import com.example.cartesanimeesapp.models.LoginRequest
import com.example.cartesanimeesapp.network.RetrofitClient
import com.example.cartesanimeesapp.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val patientId = prefs.getInt("patient_id", -1)

        if (patientId == -1) {
            // Pas connecté → aller vers LoginFragment
            findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
        } else {
            // Déjà connecté → aller vers HomeFragment
            findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
