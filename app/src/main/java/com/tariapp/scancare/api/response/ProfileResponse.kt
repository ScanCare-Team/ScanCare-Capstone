package com.tariapp.scancare.api.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("user")
	val user: User,

	@field:SerializedName("status")
	val status: String
)

data class User(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("email")
	val email: String
)
