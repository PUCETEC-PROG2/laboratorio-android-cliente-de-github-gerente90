package ec.edu.uisek.githubclient.models

import com.google.gson.annotations.SerializedName

data class RepoOwner(
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String
)
