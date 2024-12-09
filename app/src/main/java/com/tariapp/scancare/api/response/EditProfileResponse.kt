package com.tariapp.scancare.api.response

import com.google.gson.annotations.SerializedName

data class EditProfileResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null,
)
