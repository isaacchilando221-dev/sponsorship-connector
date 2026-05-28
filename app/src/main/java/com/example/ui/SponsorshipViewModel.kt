package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiService
import com.example.data.api.VerificationResult
import com.example.data.database.AppliedScholarship
import com.example.data.database.SponsorshipDatabase
import com.example.data.database.SponsorshipRepository
import com.example.data.database.StudentProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Opportunity(
    val id: Int,
    val name: String,
    val type: String,
    val location: String,
    val support: String,
    val verified: Boolean,
    val source: String,
    val description: String,
    val website: String,
    val deadline: String
)

sealed interface VerificationUiState {
    object Idle : VerificationUiState
    object Loading : VerificationUiState
    data class Success(val result: VerificationResult) : VerificationUiState
    data class Error(val message: String) : VerificationUiState
}

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface ChatUiState {
    object Idle : ChatUiState
    object Loading : ChatUiState
    data class Success(val reply: String) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

class SponsorshipViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SponsorshipDatabase.getDatabase(application)
    private val repository = SponsorshipRepository(db.sponsorshipDao())

    // --- Static Opportunities Catalog ---
    val institutions = listOf(
        Opportunity(
            id = 1,
            name = "Mastercard Foundation Scholars Program",
            type = "Global Scholarship",
            location = "Africa & International (Johannesburg, South Africa)",
            support = "Full Tuition + Living Expenses",
            verified = true,
            source = "Official University Partnerships",
            description = "Provides scholarships to academically talented students facing financial barriers. Highly popular across Africa.",
            website = "https://mastercardfdn.org/all/scholars-program/",
            deadline = "September 15, 2026"
        ),
        Opportunity(
            id = 2,
            name = "UNICEF Education Support",
            type = "Educational Assistance",
            location = "Global",
            support = "School Support & Educational Aid",
            verified = true,
            source = "UNICEF Official Programs",
            description = "Supports vulnerable learners and school leavers through educational initiatives and local study support.",
            website = "https://www.unicef.org/education",
            deadline = "Rolling Applications"
        ),
        Opportunity(
            id = 3,
            name = "DAAD Scholarships",
            type = "University Scholarship",
            location = "Germany, Europe & International",
            support = "Tuition + Monthly Stipend",
            verified = true,
            source = "German Academic Exchange Service",
            description = "Provides funding opportunities for undergraduate and postgraduate studies in Germany/Europe.",
            website = "https://www.daad.de/en/",
            deadline = "October 31, 2026"
        ),
        Opportunity(
            id = 4,
            name = "Chevening Scholarships",
            type = "Government Scholarship",
            location = "United Kingdom, Europe",
            support = "Full Sponsorship",
            verified = true,
            source = "UK Government Office",
            description = "UK government international scholarship programme for global future leaders studying at UK institutions.",
            website = "https://www.chevening.org/",
            deadline = "November 5, 2026"
        ),
        Opportunity(
            id = 5,
            name = "Sallie Mae Professional Student Loans",
            type = "Academic Student Loan",
            location = "North America (United States / Texas / New York / California)",
            support = "Flexible Repayment + Full Tuition Funding",
            verified = true,
            source = "Sallie Mae Financial",
            description = "Highly reliable and dynamic private student loan option with competitive rates for undergraduate and career training.",
            website = "https://www.salliemae.com/",
            deadline = "Rolling Applications"
        ),
        Opportunity(
            id = 6,
            name = "Zanaco Academic Student Shield",
            type = "Academic Student Loan",
            location = "Africa (Zambia / Lusaka / Copperbelt)",
            support = "Low Interest Study Financing",
            verified = true,
            source = "Zambia National Commercial Bank",
            description = "Tailored credit facilities and low-interest student development loans for tuition and accommodation in Africa.",
            website = "https://www.zanaco.co.zm/",
            deadline = "August 31, 2026"
        ),
        Opportunity(
            id = 7,
            name = "Ontario Student Assistance Program (OSAP)",
            type = "Government Loan & Grants",
            location = "North America (Canada / Ontario / Toronto)",
            support = "Partially Forgiving Government Loans",
            verified = true,
            source = "Ministry of Colleges and Universities",
            description = "Provincial student financial assistance application offering combinations of grants (free money) and student loans for college/university.",
            website = "https://www.ontario.ca/page/osap-ontario-student-assistance-program",
            deadline = "December 1, 2026"
        ),
        Opportunity(
            id = 8,
            name = "Indian National Overseas Scholarship",
            type = "Government fellowship",
            location = "Asia (India / Delhi / Mumbai)",
            support = "Full Foreign Travel + Tuition Coverage",
            verified = true,
            source = "Ministry of Social Justice and Empowerment",
            description = "Funding support for low-income scholars from India pursuing Masters or Ph.D degrees abroad.",
            website = "https://nosmsje.gov.in/",
            deadline = "October 10, 2026"
        ),
        Opportunity(
            id = 9,
            name = "Australia Awards Regional Scholarships",
            type = "Government Scholarship",
            location = "Oceania/Asia (Australia / Sydney / Melbourne)",
            support = "Full Support Grants & Work Allowances",
            verified = true,
            source = "Department of Foreign Affairs and Trade",
            description = "Long-term study and research support in Australia, targeting exceptional students from global partner nations.",
            website = "https://www.dfat.gov.au/people-to-people/australia-awards/Pages/australia-awards-scholarships",
            deadline = "April 30, 2027"
        )
    )

    // --- UI State StateFlows ---
    val studentProfile: StateFlow<StudentProfile?> = repository.studentProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val appliedScholarships: StateFlow<List<AppliedScholarship>> = repository.appliedScholarships
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<Set<Int>> = repository.bookmarks
        .map { list -> list.map { it.scholarshipId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private fun matchesAcronymOrInitialism(fullName: String, query: String): Boolean {
        val cleanQuery = query.replace(".", "").trim().lowercase()
        if (cleanQuery.isEmpty()) return false
        
        // 1. Direct substring check
        if (fullName.lowercase().contains(cleanQuery)) {
            return true
        }

        // 2. Common manual aliases mapping for predefined opportunities
        val aliases = mapOf(
            "Mastercard Foundation Scholars Program" to listOf("mcf", "mfsp", "mcfsp", "mastercard"),
            "UNICEF Education Support" to listOf("unicef", "ues"),
            "DAAD Scholarships" to listOf("daad", "ds"),
            "Chevening Scholarships" to listOf("chevening", "cs")
        )

        // Check manual aliases
        for ((key, list) in aliases) {
            if (fullName.equals(key, ignoreCase = true)) {
                if (list.any { it.contains(cleanQuery) || cleanQuery.contains(it) }) {
                    return true
                }
            }
        }

        // 3. Dynamic acronym match for robustness with future added institutions
        val words = fullName.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotEmpty() }
        
        // 1st Letter Acronym: e.g. "Mastercard Foundation" -> "mf"
        val standardAcronym = words.mapNotNull { it.firstOrNull()?.lowercaseChar() }.joinToString("")
        if (standardAcronym.contains(cleanQuery)) {
            return true
        }

        // Syllable / uppercase transition or common abbreviation parts
        val customAcronymParts = mutableListOf<Char>()
        for (word in words) {
            if (word.isNotEmpty()) {
                customAcronymParts.add(word[0].lowercaseChar())
                val lowerWord = word.lowercase()
                if (lowerWord.startsWith("master") && lowerWord.length > 6) {
                    val index = lowerWord.indexOf("card")
                    if (index != -1) {
                        customAcronymParts.add('c')
                    }
                }
                for (i in 1 until word.length) {
                    if (word[i].isUpperCase()) {
                        customAcronymParts.add(word[i].lowercaseChar())
                    }
                }
            }
        }
        val advancedAcronym = customAcronymParts.joinToString("")
        if (advancedAcronym.contains(cleanQuery)) {
            return true
        }

        // Match if letters of query form a subsequence of standard initials
        var queryIdx = 0
        for (char in standardAcronym) {
            if (queryIdx < cleanQuery.length && char == cleanQuery[queryIdx]) {
                queryIdx++
            }
        }
        if (queryIdx == cleanQuery.length) return true

        return false
    }

    // Filtered static opportunities with geographic sorting
    val filteredOpportunities: StateFlow<List<Opportunity>> = combine(
        searchQuery,
        studentProfile,
        bookmarks
    ) { query, profile, bookmarkedIds ->
        val result = if (query.isBlank()) {
            institutions
        } else {
            institutions.filter {
                matchesAcronymOrInitialism(it.name, query) ||
                        matchesAcronymOrInitialism(it.type, query) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true) ||
                        it.support.contains(query, ignoreCase = true)
            }
        }

        if (profile?.isRegistered == true) {
            val userCont = profile.continent.trim().lowercase()
            val userCountry = profile.country.trim().lowercase()
            val userState = profile.stateProvince.trim().lowercase()

            result.sortedWith(compareByDescending { opp ->
                var score = 0
                val oppLoc = opp.location.lowercase()
                val oppDesc = opp.description.lowercase()
                val oppName = opp.name.lowercase()

                if (userCont.isNotEmpty() && (oppLoc.contains(userCont) || oppDesc.contains(userCont))) score += 5
                if (userCountry.isNotEmpty() && (oppLoc.contains(userCountry) || oppDesc.contains(userCountry) || oppName.contains(userCountry))) score += 10
                if (userState.isNotEmpty() && (oppLoc.contains(userState) || oppDesc.contains(userState) || oppName.contains(userState))) score += 15
                
                // Boost matching search query terms relative to student requirements (e.g. loans / student loans)
                score
            })
        } else {
            result
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), institutions)

    // --- AI Verification State ---
    private val _verificationUiState = MutableStateFlow<VerificationUiState>(VerificationUiState.Idle)
    val verificationUiState: StateFlow<VerificationUiState> = _verificationUiState.asStateFlow()

    // --- AI Chat Assistant State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ai",
                text = "Hello! I am your Sponsorship Intelligent Counsel. Ask me any application questions, deadline details, eligibility requirements, or cover letter tips!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatUiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val chatUiState: StateFlow<ChatUiState> = _chatUiState.asStateFlow()

    // --- Actions ---
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun submitProfile(
        name: String,
        email: String,
        field: String,
        education: String,
        needs: String,
        imageUri: String? = null,
        pdfUri: String? = null,
        pdfName: String? = null,
        continent: String = "",
        country: String = "",
        stateProvince: String = ""
    ) {
        viewModelScope.launch {
            val current = repository.getProfileDirect()
            val updatedProfile = StudentProfile(
                fullName = name,
                email = email,
                fieldOfStudy = field,
                educationLevel = education,
                financialNeed = needs,
                profileImageUri = imageUri ?: current?.profileImageUri,
                pdfDocumentUri = pdfUri ?: current?.pdfDocumentUri,
                pdfDocumentName = pdfName ?: current?.pdfDocumentName,
                isRegistered = true,
                continent = continent,
                country = country,
                stateProvince = stateProvince
            )
            repository.saveProfile(updatedProfile)
        }
    }

    fun updateProfilePicture(uri: String?) {
        viewModelScope.launch {
            val current = repository.getProfileDirect() ?: StudentProfile()
            repository.saveProfile(current.copy(profileImageUri = uri))
        }
    }

    fun updatePdfDocument(uri: String?, name: String?) {
        viewModelScope.launch {
            val current = repository.getProfileDirect() ?: StudentProfile()
            repository.saveProfile(current.copy(pdfDocumentUri = uri, pdfDocumentName = name))
        }
    }

    fun applyForOpportunity(opportunity: Opportunity) {
        viewModelScope.launch {
            repository.applyForScholarship(opportunity.id, opportunity.name)
        }
    }

    fun withdrawApplication(opportunityId: Int) {
        viewModelScope.launch {
            repository.cancelApplication(opportunityId)
        }
    }

    fun toggleBookmark(opportunityId: Int) {
        viewModelScope.launch {
            val current = bookmarks.value.contains(opportunityId)
            repository.toggleBookmark(opportunityId, !current)
        }
    }

    fun runVerification(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _verificationUiState.value = VerificationUiState.Loading
            try {
                val result = GeminiService.verifyScholarship(text)
                _verificationUiState.value = VerificationUiState.Success(result)
            } catch (e: Exception) {
                _verificationUiState.value = VerificationUiState.Error(e.localizedMessage ?: "Unknown processing error")
            }
        }
    }

    fun resetVerification() {
        _verificationUiState.value = VerificationUiState.Idle
    }

    fun sendChatMessage(query: String) {
        if (query.isBlank()) return
        val userMsg = ChatMessage(sender = "user", text = query)
        _chatMessages.update { it + userMsg }
        _chatUiState.value = ChatUiState.Loading

        viewModelScope.launch {
            try {
                // Incorporate student's profile and applied list for contextual responses if registered!
                val currentProfile = repository.getProfileDirect()
                val profileContext = if (currentProfile?.isRegistered == true) {
                    "Student profile details: ${currentProfile.fullName}, studying ${currentProfile.fieldOfStudy} at ${currentProfile.educationLevel} level. Needs: ${currentProfile.financialNeed}. Geographic details: Continent: ${currentProfile.continent}, Country: ${currentProfile.country}, State/Province: ${currentProfile.stateProvince}."
                } else {
                    "No profile registered yet."
                }
                
                val reply = GeminiService.askAssistant(query, profileContext)
                _chatMessages.update { it + ChatMessage(sender = "ai", text = reply) }
                _chatUiState.value = ChatUiState.Success(reply)
            } catch (e: Exception) {
                val errMsg = "Error: ${e.localizedMessage ?: "Could not reply text"}"
                _chatMessages.update { it + ChatMessage(sender = "ai", text = errMsg) }
                _chatUiState.value = ChatUiState.Error(errMsg)
            } finally {
                _chatUiState.value = ChatUiState.Idle
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "ai",
                text = "Hello! I am your Sponsorship Intelligent Counsel. Ask me any application questions, deadline details, eligibility requirements, or cover letter tips!"
            )
        )
    }
}
