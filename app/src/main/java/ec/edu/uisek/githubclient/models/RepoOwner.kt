package ec.edu.uisek.githubclient.models

data class RepoOwner(
    val id: Long,
    val login: String,
    @SerializedName(value="avatar_url")
    val avatarUrl: String
)
