package ec.edu.uisek.githubclient.services

import android.util.Log
import ec.edu.uisek.githubclient.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.github.com/"

    // Interceptor para añadir el token de GitHub si existe
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()

        val token = BuildConfig.GITHUB_API_TOKEN
        if (token.isNotBlank()) {
            builder.header("Authorization", "token $token")
        }

        val request = builder.build()
        Log.d("RetrofitClient", "Request URL: ${request.url}")
        chain.proceed(request)
    }

    // Logs de red (solo en debug)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val gitHubApiService: GitHubApiService = retrofit.create(GitHubApiService::class.java)
}
