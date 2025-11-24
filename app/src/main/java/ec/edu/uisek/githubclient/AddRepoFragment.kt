package ec.edu.uisek.githubclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ec.edu.uisek.githubclient.databinding.FragmentAddRepoBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRepoFragment : Fragment() {

    // Patrón recomendado para viewBinding en Fragments
    private var _binding: FragmentAddRepoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRepoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val description = binding.inputDescription.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Nombre requerido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "name" to name,
                "description" to description
            )

            RetrofitClient.gitHubApiService.createRepo(body)
                .enqueue(object : Callback<Repo> {
                    override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Repositorio creado",
                                Toast.LENGTH_LONG
                            ).show()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Repo>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "Error de conexión",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}
