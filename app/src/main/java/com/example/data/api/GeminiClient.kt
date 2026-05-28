package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "responseMimeType") val responseMimeType: String? = null,
    @Json(name = "temperature") val temperature: Double? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

// Application-specific parsed AI items
@JsonClass(generateAdapter = true)
data class VerificationResult(
    @Json(name = "verificationScore") val verificationScore: Int,
    @Json(name = "trustLevel") val trustLevel: String, // "HIGH", "MEDIUM", "LOW"
    @Json(name = "flags") val flags: List<String> = emptyList(),
    @Json(name = "officialSource") val officialSource: String = "",
    @Json(name = "analysis") val analysis: String = "",
    @Json(name = "recommendation") val recommendation: String = ""
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiRetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    val moshiParser: Moshi get() = moshi
}

object GeminiService {
    private val apiService = GeminiRetrofitClient.service

    suspend fun verifyScholarship(scholarshipText: String): VerificationResult {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Safe fallback if API key is not configured or in testing model
            return VerificationResult(
                verificationScore = 45,
                trustLevel = "MEDIUM",
                flags = listOf("Offline Fallback Mode: Custom Gemini Key not set in Secrets Dashboard"),
                officialSource = "Unknown",
                analysis = "Please configure your GEMINI_API_KEY in the AI Studio Secrets panel. This is a local analysis: the content resembles common student aid opportunities but requires verification.",
                recommendation = "Register a real Gemini API Key to unlock real-time active truth analysis."
            )
        }

        val promptText = """
            Verify the following scholarship opportunity details for potential fraud, authenticity, and credentials.
            Analyze factors like:
            - Requiring heavy application fees (major red flag)
            - Contact emails like gmail.com / yahoo.com rather than official institutional domains
            - Over-promising full rides with no criteria
            - Deadlines or URLs.
            
            Return a JSON object STRICTLY containing these exact keys with correct data types:
            {
              "verificationScore": integer (0 to 100),
              "trustLevel": "HIGH" or "MEDIUM" or "LOW",
              "flags": string array of warning/trust markers,
              "officialSource": official website/portal string or empty,
              "analysis": detailed breakdown of research/authenticity analysis,
              "recommendation": safety steps for the student
            }
            Do NOT include any markdown code blocks (like ```json). Respond with the raw JSON text only.
            
            Opportunity text to analyze:
            $scholarshipText
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = promptText)))
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2
            )
        )

        return try {
            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!jsonText.isNullOrEmpty()) {
                val adapter = GeminiRetrofitClient.moshiParser.adapter(VerificationResult::class.java)
                adapter.fromJson(jsonText) ?: throw Exception("JSON conversion returned null")
            } else {
                throw Exception("No text response received from Gemini model")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            VerificationResult(
                verificationScore = 0,
                trustLevel = "LOW",
                flags = listOf("API Error: ${e.localizedMessage}"),
                officialSource = "Network Failure",
                analysis = "Error talking to Gemini API: ${e.message}",
                recommendation = "Check your internet connection or verify your API key."
            )
        }
    }

    suspend fun askAssistant(userQuery: String, contextData: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Hi there! I am your AI Sponsorship Assistant. Currently, the custom Gemini API Key is not set in your Secrets panel. Here's a helpful automatic guide: To prepare a competitive scholarship application, focus on high academic marks, draft a powerful personal motivation letter, and acquire two professional recommendations."
        }

        val systemInstructionText = """
            You are a helpful, professional, and compassionate AI Scholarship & Sponsorship Counselor.
            You help students and school leavers understand educational funding, eligibility criteria, deadline compliance, and application procedures.
            Keep explanations encouraging, clear, and focused on enabling educational equity.
            Always remind students never to pay money for application processing fees.
        """.trimIndent()

        val fullPrompt = """
            Based on the student's question and current context, answer their inquiry effectively.
            Context: $contextData
            
            Student Question: $userQuery
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = fullPrompt)))
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemInstructionText))),
            generationConfig = GenerationConfig(temperature = 0.7)
        )

        return try {
            val response = apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I couldn't generate a response. Please try in a moment."
        } catch (e: Exception) {
            "Error querying counselor: ${e.localizedMessage}"
        }
    }
}
