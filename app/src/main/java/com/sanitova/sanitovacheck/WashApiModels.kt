package com.sanitova.sanitovacheck

import com.google.gson.annotations.SerializedName

data class ChatRequest(val message: String)
data class ChatResponse(val reply: String)

data class AssessRequest(
    @SerializedName("case_id") val caseId: String? = null,
    val location: String,
    @SerializedName("report_channel") val reportChannel: String = "photo",
    @SerializedName("incident_description") val incidentDescription: String,
    @SerializedName("reporter_name") val reporterName: String? = null,
    @SerializedName("reported_date") val reportedDate: String? = null,
    @SerializedName("images_base64") val imagesBase64: List<String>? = null
)

data class AssessResponse(
    @SerializedName("case_id") val caseId: String,
    val assessment: Assessment,
    val routing: Routing,
    @SerializedName("photo_verification") val photoVerification: PhotoVerification? = null
)

data class PhotoVerification(
    val matches: Boolean?,
    val confidence: Int?,
    val explanation: String?,
    @SerializedName("ai_observed_description") val aiObservedDescription: String? = null
)

data class Assessment(
    @SerializedName("risk_level") val riskLevel: String,       // "high" | "medium" | "low"
    @SerializedName("risk_score") val riskScore: Int,
    @SerializedName("key_concerns") val keyConcerns: List<String>,
    @SerializedName("recommended_action") val recommendedAction: String,
    @SerializedName("requires_human_review") val requiresHumanReview: Boolean,
    val summary: String
)

data class Routing(
    @SerializedName("escalate_immediately") val escalateImmediately: Boolean,
    @SerializedName("assign_to") val assignTo: String,
    @SerializedName("sla_hours") val slaHours: Int
)
