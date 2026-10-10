package com.example.fuelflex.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        if (originalRequest.header("No-Auth") == "true") {
            return chain.proceed(
                originalRequest.newBuilder()
                    .removeHeader("No-Auth")
                    .build()
            )
        }

        val token = context
            .getSharedPreferences(
                "auth",
                Context.MODE_PRIVATE
            )
            .getString("access_token", null)

        val request = originalRequest
            .newBuilder()
            .apply {
                if (!token.isNullOrBlank()) {
                    addHeader(
                        "Authorization",
                        "Bearer $token"
                    )
                }
            }
            .build()

        return chain.proceed(request)
    }
}