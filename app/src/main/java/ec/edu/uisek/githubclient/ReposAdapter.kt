package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposAdapter(
    private val onEdit: (Repo) -> Unit,
    private val onDelete: (Repo) -> Unit
) : RecyclerView.Adapter<ReposAdapter.RepoViewHolder>() {

    private val repositories = mutableListOf<Repo>()

    inner class RepoViewHolder(val binding: FragmentRepoItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoViewHolder(binding)
    }

    override fun getItemCount(): Int = repositories.size

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = repositories[position]

        with(holder.binding) {
            repoName.text = repo.name
            repoDescription.text = repo.description ?: "Sin descripción"
            repoLang.text = repo.language ?: "Lenguaje no especificado"

            Glide.with(root.context)
                .load(repo.owner.avatarUrl)
                .into(repoOwnerImage)

            btnEdit.setOnClickListener { onEdit(repo) }
            btnDelete.setOnClickListener { onDelete(repo) }
        }
    }

    fun updateRepositories(newRepos: List<Repo>) {
        repositories.clear()
        repositories.addAll(newRepos)
        notifyDataSetChanged()
    }
}
