package com.tariapp.scancare.api.response

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PredictResponse(

	@field:SerializedName("hazardous_materials")
	val hazardousMaterials: List<HazardousMaterialsItem>,

	@field:SerializedName("predicted_skin_types")  // Menambahkan field untuk predicted_skin_types
	val predictedSkinTypes: List<String> // Menambahkan properti untuk skin types
)

@Parcelize
data class HazardousMaterialsItem(

	@field:SerializedName("Bahan Berbahaya")
	val bahanBerbahaya: String,

	@field:SerializedName("Analisis")
	val analisis: String
): Parcelable
