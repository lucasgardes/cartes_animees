package com.example.cartesanimeesapp.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cartesanimeesapp.R
import com.example.cartesanimeesapp.network.RetrofitClient
import com.example.cartesanimeesapp.models.LoginRequest
import com.example.cartesanimeesapp.models.LoginResponse
import com.example.cartesanimeesapp.network.Constants
import com.example.cartesanimeesapp.ui.login.LoginFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val usernameField = view.findViewById<EditText>(R.id.etUsername)
        val passwordField = view.findViewById<EditText>(R.id.etPassword)
        val loginButton = view.findViewById<Button>(R.id.btnLogin)

        loginButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                performLogin(username, password)
            }
        }

        return view
    }

    private fun performLogin(email: String, password: String) {
        RetrofitClient.apiService.loginPatient(email = email, password = password, token = Constants.API_TOKEN)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        when (result.status) {
                            "need_password" -> {
                                Toast.makeText(requireContext(), "Veuillez créer un mot de passe", Toast.LENGTH_SHORT).show()

                                // Tu peux passer l'ID du patient à CreatePasswordFragment avec SafeArgs ou Bundle
                                val action = LoginFragmentDirections.actionLoginFragmentToCreatePasswordFragment(result.patient_id ?: -1)
                                findNavController().navigate(action)
                            }

                            "success" -> {
                                Toast.makeText(requireContext(), "Connexion réussie", Toast.LENGTH_SHORT).show()

                                // Enregistrer le patient_id dans SharedPreferences
                                val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
                                prefs.edit()
                                    .putInt("patient_id", result.patient_id ?: -1)
                                    .putString("email", email)
                                    .putString("password", password)
                                    .apply()

                                // Redirection vers HomeFragment
                                findNavController().navigate(R.id.action_login_to_home)
                            }

                            else -> {
                                Toast.makeText(requireContext(), "Erreur : ${result.error}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Échec de connexion", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
