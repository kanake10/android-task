package com.example.vero.api.dtos

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("oauth") val oauth: OAuthData,
    @SerializedName("userInfo") val userInfo: UserInfo,
    @SerializedName("permissions") val permissions: List<String>,
    @SerializedName("apiVersion") val apiVersion: String,
    @SerializedName("showPasswordPrompt") val showPasswordPrompt: Boolean
)

data class OAuthData(
    @SerializedName("access_token") val access_token: String,
    @SerializedName("expires_in") val expires_in: Int,
    @SerializedName("token_type") val token_type: String,
    @SerializedName("scope") val scope: String?,
    @SerializedName("refresh_token") val refresh_token: String
)

data class UserInfo(
    @SerializedName("personalNo") val personalNo: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("active") val active: Boolean,
    @SerializedName("businessUnit") val businessUnit: String
)


data class AuthRequest(
    val username: String,
    val password: String
)