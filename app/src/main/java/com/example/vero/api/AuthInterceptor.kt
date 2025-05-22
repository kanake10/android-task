package com.example.vero.api

import com.example.vero.iteractor.AuthDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authDataStore: AuthDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Skip adding token for login endpoint
        if (request.url.encodedPath.contains("/login")) {
            return chain.proceed(request)
        }

        val token = runBlocking {
            authDataStore.authToken.firstOrNull()
        }

        val newRequest = request.newBuilder().apply {
            token?.let {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(newRequest)
    }
}