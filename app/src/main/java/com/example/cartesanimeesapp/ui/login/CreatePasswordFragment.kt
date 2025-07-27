package com.example.cartesanimeesapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cartesanimeesapp.R
import com.example.cartesanimeesapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.navigation.fragment.navArgs
import com.example.cartesanimeesapp.network.Constants

class CreatePasswordFragment : Fragment() {

    private val args: CreatePasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val patientId = args.patientId
        val passwordField = view.findViewById<EditText>(R.id.etPassword)
        val confirmField = view.findViewById<EditText>(R.id.etConfirmPassword)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitPassword)

        btnSubmit.setOnClickListener {
            val pass = passwordField.text.toString()
            val confirm = confirmField.text.toString()

            if (pass.length < 6) {
                Toast.makeText(requireContext(), "Mot de passe trop court", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != confirm) {
                Toast.makeText(requireContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.apiService.setPatientPassword(
                patientId = patientId,
                password = pass,
                token = Constants.API_TOKEN
            ).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Mot de passe enregistré", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_createPasswordFragment_to_loginFragment)
                    } else {
                        Toast.makeText(requireContext(), "Erreur serveur", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Erreur réseau", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
