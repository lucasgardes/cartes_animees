package com.example.cartesanimeesapp.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cartesanimeesapp.R
import com.example.cartesanimeesapp.databinding.FragmentProfileBinding
import com.example.cartesanimeesapp.network.RetrofitClient
import com.example.cartesanimeesapp.models.Subscription
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import com.example.cartesanimeesapp.network.Constants

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val email = prefs.getString("email", "Inconnu")
        binding.tvEmail.text = "Email : $email"

        val patientId = prefs.getInt("patient_id", -1)
        if (patientId != -1) {
            RetrofitClient.apiService.getSubscription(patientId = patientId, token = Constants.API_TOKEN)
                .enqueue(object : Callback<Subscription?> {
                    override fun onResponse(
                        call: Call<Subscription?>,
                        response: Response<Subscription?>
                    ) {
                        if (response.isSuccessful) {
                            val sub = response.body()
                            val subText = sub?.stripe_subscription_id ?: "Aucun"
                            binding.tvAbonnement.text = "Abonnement : $subText"
                            prefs.edit().putString("subscription", subText).apply()

                            if (sub != null && sub.stripe_subscription_id != null && sub.stripe_subscription_id != "Aucun") {
                                binding.btnResilier.visibility = View.VISIBLE
                            } else {
                                binding.btnResilier.visibility = View.GONE
                            }
                        } else {
                            binding.tvAbonnement.text = "Abonnement : Erreur"
                        }
                    }

                    override fun onFailure(call: Call<Subscription?>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "Erreur de connexion pour l'abonnement",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.tvAbonnement.text = "Abonnement : Erreur"
                    }
                })
        } else {
            binding.tvAbonnement.text = "Abonnement : Inconnu"
        }

        binding.btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            findNavController().navigate(R.id.loginFragment)
        }

        binding.btnBackHome.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }

        binding.btnResilier.setOnClickListener {
            showPasswordConfirmation {
                if (patientId != -1) {
                    RetrofitClient.apiService.requestResiliation(patientId = patientId, token = Constants.API_TOKEN)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Abonnement résilié",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    prefs.edit().putString("subscription", "Aucun").apply()
                                    binding.tvAbonnement.text = "Abonnement : Aucun"
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Erreur serveur",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(
                                    requireContext(),
                                    "Erreur réseau",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    Toast.makeText(requireContext(), "ID patient non trouvé", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showPasswordConfirmation(onConfirmed: () -> Unit) {
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        input.hint = "Mot de passe"

        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation de mot de passe")
            .setMessage("Veuillez entrer votre mot de passe pour résilier l'abonnement.")
            .setView(input)
            .setPositiveButton("Confirmer") { _, _ ->
                val enteredPassword = input.text.toString()
                val storedPassword = getPasswordFromPrefs()
                if (enteredPassword == storedPassword) {
                    onConfirmed()
                } else {
                    Toast.makeText(context, "Mot de passe incorrect", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun getPasswordFromPrefs(): String? {
        val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        return prefs.getString("password", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
