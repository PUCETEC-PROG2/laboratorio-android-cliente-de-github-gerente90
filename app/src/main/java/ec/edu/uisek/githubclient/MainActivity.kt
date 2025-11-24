package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFab()
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(
            onEdit = { repo -> openEditRepo(repo) },
            onDelete = { repo -> confirmDeleteRepo(repo) }
        )
        binding.repoRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = reposAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddRepo.setOnClickListener {
            val fragment = AddRepoFragment.newInstanceForCreate()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun fetchRepositories() {
        val call = RetrofitClient.gitHubApiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(
                call: Call<List<Repo>>,
                response: Response<List<Repo>>
            ) {
                if (response.isSuccessful) {
                    val repos = response.body().orEmpty()
                    if (repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios.")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "No autorizado (401)"
                        403 -> "Prohibido (403)"
                        404 -> "No encontrado (404)"
                        else -> "Error desconocido (${response.code()})"
                    }
                    Log.e("MainActivity", "API Error: $errorMsg")
                    showMessage("Error: $errorMsg")
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.e("MainActivity", "Fallo en la API", t)
                showMessage("Error de conexión: ${t.localizedMessage ?: "desconocido"}")
            }
        })
    }

    private fun openEditRepo(repo: Repo) {
        val fragment = AddRepoFragment.newInstanceForEdit(repo)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun confirmDeleteRepo(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar repositorio")
            .setMessage("¿Seguro que quieres eliminar \"${repo.name}\"? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteRepo(repo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteRepo(repo: Repo) {
        val owner = repo.owner.login

        RetrofitClient.gitHubApiService.deleteRepo(owner, repo.name)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        showMessage("Repositorio eliminado")
                        fetchRepositories()
                    } else {
                        showMessage("Error al eliminar: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showMessage("Error de conexión al eliminar")
                }
            })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
