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

    private var _binding: FragmentAddRepoBinding? = null
    private val binding get() = _binding!!

    private var isEditing = false
    private var repoName: String? = null
    private var repoOwner: String? = null
    private var initialDescription: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            repoName = it.getString(ARG_REPO_NAME)
            repoOwner = it.getString(ARG_REPO_OWNER)
            initialDescription = it.getString(ARG_REPO_DESC)
            isEditing = repoName != null && repoOwner != null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRepoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (isEditing) {
            binding.inputName.setText(repoName)
            binding.inputName.isEnabled = false            // nombre no editable
            binding.inputDescription.setText(initialDescription ?: "")
        }

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

            if (isEditing) {
                updateRepo(description)
            } else {
                createRepo(name, description)
            }
        }
    }

    private fun createRepo(name: String, description: String) {
        val body = mapOf(
            "name" to name,
            "description" to description
        )

        RetrofitClient.gitHubApiService.createRepo(body)
            .enqueue(object : Callback<Repo> {
                override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Repositorio creado", Toast.LENGTH_LONG).show()
                        (activity as? MainActivity)?.fetchRepositories()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Repo>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateRepo(description: String) {
        val owner = repoOwner ?: return
        val name = repoName ?: return

        val body = mapOf("description" to description)

        RetrofitClient.gitHubApiService.updateRepo(owner, name, body)
            .enqueue(object : Callback<Repo> {
                override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Repositorio actualizado", Toast.LENGTH_LONG).show()
                        (activity as? MainActivity)?.fetchRepositories()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Repo>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_REPO_NAME = "arg_repo_name"
        private const val ARG_REPO_OWNER = "arg_repo_owner"
        private const val ARG_REPO_DESC = "arg_repo_desc"

        fun newInstanceForCreate(): AddRepoFragment = AddRepoFragment()

        fun newInstanceForEdit(repo: Repo): AddRepoFragment =
            AddRepoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_REPO_NAME, repo.name)
                    putString(ARG_REPO_OWNER, repo.owner.login)
                    putString(ARG_REPO_DESC, repo.description ?: "")
                }
            }
    }
}
