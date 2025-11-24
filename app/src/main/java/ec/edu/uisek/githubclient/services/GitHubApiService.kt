package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body


interface GitHubApiService {

    @GET("user/repos")
    fun getRepos(): Call<List<Repo>>

    @POST("user/repos")
    fun createRepo(@Body body: Map<String, String>): Call<Repo>

}
