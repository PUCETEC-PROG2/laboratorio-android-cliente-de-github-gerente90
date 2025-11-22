package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposAdapter : RecyclerView.Adapter<ReposAdapter.RepoViewHolder>() {

    private val repositories = mutableListOf<Repo>()

    inner class RepoViewHolder(
        val binding: FragmentRepoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repo) {
            binding.tvRepoName.text = repo.name
            binding.tvRepoDescription.text =
                repo.description ?: "Sin descripción"
            binding.tvRepoLanguage.text =
                repo.language ?: "Lenguaje desconocido"

            Glide.with(binding.root.context)
                .load(repo.owner.avatarUrl)
                .into(binding.ivOwnerAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentRepoItemBinding.inflate(inflater, parent, false)
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    override fun getItemCount(): Int = repositories.size

    fun updateRepositories(repos: List<Repo>) {
        repositories.clear()
        repositories.addAll(repos)
        notifyDataSetChanged()
    }
}
