package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyRow
import com.example.data.database.StudentProfile
import com.example.data.database.AppliedScholarship
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

data class CertifiedInstitution(
    val name: String,
    val description: String,
    val website: String,
    val category: String
)

data class FeaturedUniversity(
    val name: String,
    val location: String,
    val type: String,
    val description: String,
    val website: String,
    val shortCode: String
)

fun getUniversitiesForCountry(country: String?): List<FeaturedUniversity> {
    val clean = (country ?: "").trim().lowercase()
    if (clean.isEmpty()) {
        return listOf(
            FeaturedUniversity(
                name = "University of Oxford",
                location = "Oxford, England, UK",
                type = "Public Research",
                description = "Renowned collegiate research university offering the Clarendon Fund for international postgraduates.",
                website = "https://www.ox.ac.uk/",
                shortCode = "OXFORD"
            ),
            FeaturedUniversity(
                name = "Harvard University",
                location = "Cambridge, MA, USA",
                type = "Private Ivy League",
                description = "Offers robust, 100% need-blind financial aid packages covering full tuition for eligible international students.",
                website = "https://www.harvard.edu/",
                shortCode = "HARVARD"
            ),
            FeaturedUniversity(
                name = "University of Zambia",
                location = "Lusaka, Zambia",
                type = "Public University",
                description = "The premier public institution in Zambia with options for government bursaries (HELSB) and local grants.",
                website = "https://www.unza.zm/",
                shortCode = "UNZA"
            ),
            FeaturedUniversity(
                name = "University of Toronto",
                location = "Toronto, ON, Canada",
                type = "Public Research",
                description = "Houses the Lester B. Pearson International Scholarship, providing full support for outstanding global leaders.",
                website = "https://www.utoronto.ca/",
                shortCode = "U OF T"
            ),
            FeaturedUniversity(
                name = "Technical University of Munich",
                location = "Munich, Germany",
                type = "Public Elite",
                description = "An excellent option offering low-to-no tuition programs with partner DAAD sponsorship and living grants.",
                website = "https://www.tum.de/",
                shortCode = "TUM"
            )
        )
    }

    if (clean.contains("zambia")) {
        return listOf(
            FeaturedUniversity(
                name = "University of Zambia (UNZA)",
                location = "Lusaka, Zambia",
                type = "Public University",
                description = "Zambia's largest premier university. Heavily integrated with HELSB student loans and international bursary pools.",
                website = "https://www.unza.zm/",
                shortCode = "UNZA"
            ),
            FeaturedUniversity(
                name = "Copperbelt University (CBU)",
                location = "Kitwe, Copperbelt, Zambia",
                type = "Public Science & Tech",
                description = "Highly prestigious engineering and business research hub. Backed directly by national student loan schemes.",
                website = "https://www.cbu.ac.zm/",
                shortCode = "CBU"
            ),
            FeaturedUniversity(
                name = "Mulungushi University",
                location = "Kabwe, Central, Zambia",
                type = "Public University",
                description = "Dynamic university offering multi-disciplinary courses with excellent on-campus accommodation grants.",
                website = "https://www.mu.ac.zm/",
                shortCode = "MU"
            ),
            FeaturedUniversity(
                name = "Evelyn Hone College",
                location = "Lusaka, Zambia",
                type = "Public Applied Arts",
                description = "Esteemed college offering practical and vocational science diplomas, eligible for Ministry bursaries.",
                website = "https://www.evelynhone.edu.zm/",
                shortCode = "EHC"
            )
        )
    }

    if (clean.contains("usa") || clean.contains("united states") || clean.contains("america") || clean.contains("u. s.")) {
        return listOf(
            FeaturedUniversity(
                name = "Harvard University",
                location = "Cambridge, Massachusetts, USA",
                type = "Private Ivy League",
                description = "World-renowned Ivy League offering 100% need-blind financial aid for domestic and search-qualifying international scholars.",
                website = "https://www.harvard.edu/",
                shortCode = "HARVARD"
            ),
            FeaturedUniversity(
                name = "Stanford University",
                location = "Stanford, California, USA",
                type = "Private Research",
                description = "Extensive tech and entrepreneurial partnerships, housing the prestigious Knight-Hennessy Graduate Scholars.",
                website = "https://www.stanford.edu/",
                shortCode = "STANFORD"
            ),
            FeaturedUniversity(
                name = "Massachusetts Institute of Technology (MIT)",
                location = "Cambridge, Massachusetts, USA",
                type = "Private STEM Elite",
                description = "Premier technology research university. Offers extensive direct scholarships and academic research stipends.",
                website = "https://www.mit.edu/",
                shortCode = "MIT"
            ),
            FeaturedUniversity(
                name = "University of California, Berkeley",
                location = "Berkeley, California, USA",
                type = "Public Ivy",
                description = "Top-rated public university in the US. Offers generous Cal Grants, financial aid, and state student loans.",
                website = "https://www.berkeley.edu/",
                shortCode = "UCB"
            )
        )
    }

    if (clean.contains("canada") || clean.contains("ontario") || clean.contains("quebec") || clean.contains("toronto")) {
        return listOf(
            FeaturedUniversity(
                name = "University of Toronto",
                location = "Toronto, Ontario, Canada",
                type = "Public Research",
                description = "Hosts the Lester B. Pearson Scholarship covering four years of tuition, residence, books, and full support.",
                website = "https://www.utoronto.ca/",
                shortCode = "U OF T"
            ),
            FeaturedUniversity(
                name = "McGill University",
                location = "Montreal, Quebec, Canada",
                type = "Public University",
                description = "High academic prestige with over $100M CAD dispersed in student assistance, bursaries, and merit awards yearly.",
                website = "https://www.mcgill.ca/",
                shortCode = "MCGILL"
            ),
            FeaturedUniversity(
                name = "University of British Columbia",
                location = "Vancouver, British Columbia, Canada",
                type = "Public Research",
                description = "Renowned globally, dedicating millions in entrance awards and need-based international student assistance.",
                website = "https://www.ubc.ca/",
                shortCode = "UBC"
            )
        )
    }

    if (clean.contains("united kingdom") || clean.contains("uk") || clean.contains("great britain") || clean.contains("england") || clean.contains("london")) {
        return listOf(
            FeaturedUniversity(
                name = "University of Oxford",
                location = "Oxford, Oxfordshire, UK",
                type = "Public Collegiate",
                description = "Global standard. Fully integrated with Commonwealth, Chevening and Oxford's high-value Clarendon scholarships.",
                website = "https://www.ox.ac.uk/",
                shortCode = "OXFORD"
            ),
            FeaturedUniversity(
                name = "University of Cambridge",
                location = "Cambridge, Cambridgeshire, UK",
                type = "Public Collegiate",
                description = "Home of the Gates Cambridge Trust. Fully funds tuition, stipend and travel for students globally.",
                website = "https://www.cam.ac.uk/",
                shortCode = "CAM"
            ),
            FeaturedUniversity(
                name = "Imperial College London",
                location = "London, England, UK",
                type = "Public STEM Elite",
                description = "Top engineering and sciences university, featuring President’s Ph.D scholarships and science-foundation loans.",
                website = "https://www.imperial.ac.uk/",
                shortCode = "ICL"
            )
        )
    }

    if (clean.contains("india") || clean.contains("delhi") || clean.contains("mumbai") || clean.contains("bangalore")) {
        return listOf(
            FeaturedUniversity(
                name = "Indian Institute of Technology Bombay",
                location = "Mumbai, Maharashtra, India",
                type = "Public Institute of Eminence",
                description = "Top science and tech institute with extensive state-sponsored and corporate fellowship opportunities.",
                website = "https://www.iitb.ac.in/",
                shortCode = "IITB"
            ),
            FeaturedUniversity(
                name = "Indian Institute of Science",
                location = "Bengaluru, Karnataka, India",
                type = "Public Research Univ",
                description = "Elite scientific studies and post-graduate engineering courses backed by major government scholarship incentives.",
                website = "https://www.iisc.ac.in/",
                shortCode = "IISC"
            ),
            FeaturedUniversity(
                name = "University of Delhi",
                location = "New Delhi, Delhi, India",
                type = "Central Public University",
                description = "Academically renowned offering affordable fees alongside numerous national overseas scholarship partnerships.",
                website = "http://www.du.ac.in/",
                shortCode = "DU"
            )
        )
    }

    if (clean.contains("germany") || clean.contains("deutschland") || clean.contains("munich") || clean.contains("berlin")) {
        return listOf(
            FeaturedUniversity(
                name = "Technical University of Munich",
                location = "Munich, Bavaria, Germany",
                type = "Elite Public Research",
                description = "Tuition-free high tech institution with massive DAAD partnership links and stipend grants.",
                website = "https://www.tum.de/",
                shortCode = "TUM"
            ),
            FeaturedUniversity(
                name = "Heidelberg University",
                location = "Heidelberg, Baden-Württemberg, Germany",
                type = "Public Elite",
                description = "Germany's oldest university, famous for medicine and humanities. Eligible for national BAföG study support.",
                website = "https://www.uni-heidelberg.de/",
                shortCode = "HD"
            ),
            FeaturedUniversity(
                name = "Humboldt University of Berlin",
                location = "Berlin, Germany",
                type = "Public Liberal Arts/Science",
                description = "Highly competitive with extensive research budgets, linked to European Erasmus and government scholarships.",
                website = "https://www.hu-berlin.de/",
                shortCode = "HU"
            )
        )
    }

    if (clean.contains("australia") || clean.contains("sydney") || clean.contains("melbourne")) {
        return listOf(
            FeaturedUniversity(
                name = "University of Melbourne",
                location = "Melbourne, Victoria, Australia",
                type = "Public Research Univ",
                description = "Offers the Melbourne Research Scholarship and Australia Awards, securing full tuition and living allowances.",
                website = "https://www.unimelb.edu.au/",
                shortCode = "MELB"
            ),
            FeaturedUniversity(
                name = "Australian National University (ANU)",
                location = "Canberra, ACT, Australia",
                type = "Public Federal Univ",
                description = "Elite national university providing various pathway awards and direct financial aid programs with DFAT.",
                website = "https://www.anu.edu.au/",
                shortCode = "ANU"
            ),
            FeaturedUniversity(
                name = "University of Sydney",
                location = "Sydney, New South Wales, Australia",
                type = "Public Comprehensive",
                description = "Renowned for international research Excellence Scholarships, offering tuition offsets of up to 100%.",
                website = "https://www.sydney.edu.au/",
                shortCode = "USYD"
            )
        )
    }

    return listOf(
        FeaturedUniversity(
            name = "Global Elite Institution Support",
            location = "Worldwide Partnerships",
            type = "International Target",
            description = "We recommend researching national government bureaus and world bank program portals in $country.",
            website = "https://www.worldbank.org/en/programs/scholarships",
            shortCode = "GLOBAL"
        ),
        FeaturedUniversity(
            name = "University of Oxford",
            location = "Oxford, England, UK",
            type = "Public Collegiate",
            description = "Maintains worldwide scholarships for high-caliber international postgraduate students.",
            website = "https://www.ox.ac.uk/",
            shortCode = "OXFORD"
        ),
        FeaturedUniversity(
            name = "Harvard University",
            location = "Cambridge, MA, USA",
            type = "Private Ivy League",
            description = "Extends equal non-discriminatory full financial support to talented foreign nationals.",
            website = "https://www.harvard.edu/",
            shortCode = "HARVARD"
        )
    )
}

fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}

class MainActivity : ComponentActivity() {

    private val viewModel: SponsorshipViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationTab {
    EXPLORE, VERIFIER, COUNSELOR, PORTAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: SponsorshipViewModel) {
    var currentTab by remember { mutableStateOf(NavigationTab.EXPLORE) }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == NavigationTab.EXPLORE,
                    onClick = { currentTab = NavigationTab.EXPLORE },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Explore") },
                    label = { Text("Explore") },
                    modifier = Modifier.testTag("nav_explore")
                )
                NavigationBarItem(
                    selected = currentTab == NavigationTab.VERIFIER,
                    onClick = { currentTab = NavigationTab.VERIFIER },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "AI Verifier") },
                    label = { Text("AI Verifier") },
                    modifier = Modifier.testTag("nav_verifier")
                )
                NavigationBarItem(
                    selected = currentTab == NavigationTab.COUNSELOR,
                    onClick = { currentTab = NavigationTab.COUNSELOR },
                    icon = { Icon(Icons.Default.Person, contentDescription = "AI Counselor") },
                    label = { Text("AI Counselor") },
                    modifier = Modifier.testTag("nav_counselor")
                )
                NavigationBarItem(
                    selected = currentTab == NavigationTab.PORTAL,
                    onClick = { currentTab = NavigationTab.PORTAL },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                    label = { Text("Portal") },
                    modifier = Modifier.testTag("nav_portal")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Screen Transitions
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(durationMillis = 200),
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    NavigationTab.EXPLORE -> ExploreTabScreen(viewModel, context, onNavigateToPortal = { currentTab = NavigationTab.PORTAL })
                    NavigationTab.VERIFIER -> VerifierTabScreen(viewModel)
                    NavigationTab.COUNSELOR -> CounselorTabScreen(viewModel)
                    NavigationTab.PORTAL -> PortalTabScreen(viewModel, context)
                }
            }
        }
    }
}

// --- EXPLORE TAB SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreTabScreen(viewModel: SponsorshipViewModel, context: Context, onNavigateToPortal: () -> Unit) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val opportunities by viewModel.filteredOpportunities.collectAsStateWithLifecycle()
    val bookmarkIds by viewModel.bookmarks.collectAsStateWithLifecycle()
    val studentProfile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val appliedList by viewModel.appliedScholarships.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_banner"),
                shape = RoundedCornerShape(24.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    // Hero Image representing graduation gowns, success, etc.
                    Image(
                        painter = painterResource(id = R.drawable.img_graduation_hero_1779963276257),
                        contentDescription = "Graduation celebration and scholarship portal",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Dark elegant gradient overlay for readability and premium look
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Card Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700), // Elegant gold star
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sponsorship Connect",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your gateway to graduation. Explore AI-verified scholarships, financial aid, and interactive counselor guidance perfectly tailored of you.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.92f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Quick Stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Verified Orgs",
                    value = "100%",
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "My Bookmarks",
                    value = bookmarkIds.count().toString(),
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Applied",
                    value = appliedList.count().toString(),
                    icon = Icons.Default.Send,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search Bar Section
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_bar"),
                placeholder = { Text("Search scholarships, places or support types...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }

        // Search Suggestions Section Based on Acronyms or Popular Queries
        item {
            val suggestions = listOf(
                "MCF" to "Mastercard Program",
                "DAAD" to "German Exchange",
                "UNICEF" to "UNICEF Support",
                "OSAP" to "Ontario Assistance",
                "Sallie Mae" to "Student Loans",
                "Zanaco" to "Low-Interest Credit",
                "Loans" to "Student Loans",
                "Fully Funded" to "Full Support"
            )

            // Filter suggestions dynamically or show popular ones
            val filteredSuggestions = if (searchQuery.isEmpty()) {
                suggestions
            } else {
                suggestions.filter { (key, desc) ->
                    key.contains(searchQuery, ignoreCase = true) || desc.contains(searchQuery, ignoreCase = true)
                }
            }

            if (filteredSuggestions.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) "Suggested Searches" else "Matching Suggestions",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (searchQuery.isNotEmpty()) {
                            Text(
                                text = "${filteredSuggestions.size} found",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(filteredSuggestions) { (term, label) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier
                                    .clickable {
                                        viewModel.updateSearchQuery(term)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = term,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = label,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Opportunities Header
        item {
            Column {
                Text(
                    text = "Available Opportunities",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                if (studentProfile?.isRegistered == true && 
                    (studentProfile?.continent?.isNotBlank() == true || studentProfile?.country?.isNotBlank() == true || studentProfile?.stateProvince?.isNotBlank() == true)
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val isContList = listOfNotNull(
                                studentProfile?.stateProvince?.takeIf { it.isNotBlank() },
                                studentProfile?.country?.takeIf { it.isNotBlank() },
                                studentProfile?.continent?.takeIf { it.isNotBlank() }
                            ).joinToString(", ")
                            
                            Text(
                                text = "Sorting regional loans & sponsorships for: $isContList",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (opportunities.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "No results",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching sponsorships found.",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        } else {
            items(opportunities, key = { it.id }) { opp ->
                OpportunityItemCard(
                    opp = opp,
                    isBookmarked = bookmarkIds.contains(opp.id),
                    isApplied = appliedList.any { it.scholarshipId == opp.id },
                    onBookmarkToggle = { viewModel.toggleBookmark(opp.id) },
                    onApplyClick = {
                        if (studentProfile?.isRegistered == true) {
                            viewModel.applyForOpportunity(opp)
                            Toast.makeText(context, "Successfully submitted application details to ${opp.name}!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Please complete your Student Portal Profile first to enable fast One-Tap applications!", Toast.LENGTH_LONG).show()
                        }
                    },
                    onVisitClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(opp.website))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Invalid link: ${opp.website}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        // --- Local Universities & Regional Academic Directory ---
        item {
            val hasCountry = studentProfile?.isRegistered == true && studentProfile?.country?.isNotBlank() == true
            val countryName = if (hasCountry) studentProfile?.country?.trim() else ""
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasCountry) "Universities in $countryName" else "Accredited Local Universities",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (hasCountry) {
                        "Top accredited local institutions integrated with financial aid, state loans, and regional bursary programs in $countryName."
                    } else {
                        "Accredited universities with student loan integrations. Complete your profile to filter local options."
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                
                if (!hasCountry) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToPortal() }
                            .testTag("complete_profile_univ_hint")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Configure Country & State",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Tap to input your location in the Student Portal and view highly-relevant local universities!",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            val hasCountry = studentProfile?.isRegistered == true && studentProfile?.country?.isNotBlank() == true
            val countryName = if (hasCountry) studentProfile?.country?.trim() else ""
            val universityList = getUniversitiesForCountry(countryName)

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(universityList) { univ ->
                    Card(
                        modifier = Modifier
                            .width(260.dp)
                            .height(180.dp)
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(univ.website))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open: ${univ.website}", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .testTag("univ_card_${univ.shortCode}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.secondary,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = univ.type,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = "Location Pin",
                                            tint = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = univ.location.substringBefore(","),
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.outline,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Text(
                                    text = univ.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = univ.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    lineHeight = 14.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Visit Campus Portal",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Certified Sponsoring Institutions & Directories ---
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Certified Directories & Sponsors",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "These academic registries contain verified, highly trusted bursary resources approved by government & international bodies.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        item {
            val certifiedList = listOf(
                CertifiedInstitution(
                    name = "Commonwealth Scholarships",
                    description = "Highly competitive fully funded post-graduate sponsorships for scholars globally.",
                    website = "https://www.cscuk.fcdo.gov.uk/",
                    category = "Government Agency"
                ),
                CertifiedInstitution(
                    name = "World Bank Scholarships",
                    description = "Comprehensive master's programs funded for development practitioners internationally.",
                    website = "https://www.worldbank.org/en/programs/scholarships",
                    category = "Global Fin. Institution"
                ),
                CertifiedInstitution(
                    name = "Gates Cambridge Trust",
                    description = "Select academic funding for full-time postgraduate degrees at Cambridge University.",
                    website = "https://www.gatescambridge.org/",
                    category = "University Sponsor"
                ),
                CertifiedInstitution(
                    name = "Erasmus+ EU Programme",
                    description = "Official European Union hub offering academic and professional training schemes.",
                    website = "https://erasmus-plus.ec.europa.eu/",
                    category = "Intergovernmental Hub"
                ),
                CertifiedInstitution(
                    name = "US Fulbright Program",
                    description = "Leader in international educational exchange enabling research across 160+ countries.",
                    website = "https://fulbrightprogram.org/",
                    category = "Gov Educational Fund"
                )
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(certifiedList) { inst ->
                    Card(
                        modifier = Modifier
                            .width(260.dp)
                            .height(180.dp)
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inst.website))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open: ${inst.website}", Toast.LENGTH_SHORT).show()
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = inst.category,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Certified badge",
                                            tint = Color(0xFF137333),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Certified", fontSize = 10.sp, color = Color(0xFF137333), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Text(
                                    text = inst.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = inst.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    lineHeight = 14.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Visit Official Portal",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun OpportunityItemCard(
    opp: Opportunity,
    isBookmarked: Boolean,
    isApplied: Boolean,
    onBookmarkToggle: () -> Unit,
    onApplyClick: () -> Unit,
    onVisitClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("opportunity_card_${opp.id}"),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Category Tag & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = opp.type,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = opp.location, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Name
            Text(
                text = opp.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Verified Badge Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (opp.verified) Color(0xFFE6F4EA) else Color(0xFFFCE8E6)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (opp.verified) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (opp.verified) Color(0xFF137333) else Color(0xFFC5221F),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (opp.verified) "Verified Source" else "Unverified",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (opp.verified) Color(0xFF137333) else Color(0xFFC5221F)
                        )
                    }
                }
                Text(
                    text = "Source: ${opp.source}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = opp.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Support & Deadline Highlights
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "SUPPORT", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                    Text(text = opp.support, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "DEADLINE", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                    Text(text = opp.deadline, fontSize = 13.sp, color = Color(0xFFB45309), fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onApplyClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isApplied) Color(0xFF137333) else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("apply_btn_${opp.id}")
                    ) {
                        Icon(
                            imageVector = if (isApplied) Icons.Default.CheckCircle else Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isApplied) "Applied" else "Apply Now", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onVisitClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("visit_btn_${opp.id}")
                    ) {
                        Text(text = "Visit Site", fontSize = 13.sp)
                    }
                }

                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier.testTag("bookmark_btn_${opp.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Bookmark opportunity",
                        tint = if (isBookmarked) Color(0xFFD97706) else Color.LightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


// --- AI VERIFICATION TAB SCREEN ---
@Composable
fun VerifierTabScreen(viewModel: SponsorshipViewModel) {
    val uiState by viewModel.verificationUiState.collectAsStateWithLifecycle()
    var rawTextToVerify by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("verifier_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanatory Intro Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI Truth Detection Engine",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Protect yourself from educational fraud. Paste details from any message, email, or suspicious social media poster, and our Gemini model will scan for red flags, check if official domains are missing, and calculate an authenticity score.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Verification Input Text area
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Opportunity Text to Analyze",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = rawTextToVerify,
                        onValueChange = { rawTextToVerify = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .testTag("verifier_input_field"),
                        placeholder = { Text("Paste suspicious scholarship advertisements, requirements list, or offer letters here...", fontSize = 13.sp) },
                        maxLines = 10,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (rawTextToVerify.isNotEmpty()) {
                            TextButton(
                                onClick = { rawTextToVerify = "" },
                                modifier = Modifier.testTag("verifier_clear_btn")
                            ) {
                                Text("Clear")
                            }
                        }
                        Button(
                            onClick = { viewModel.runVerification(rawTextToVerify) },
                            enabled = rawTextToVerify.isNotBlank() && uiState !is VerificationUiState.Loading,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("analyze_btn")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Analyze Authenticity")
                        }
                    }
                }
            }
        }

        // Analysis Dynamic Outputs
        item {
            AnimatedContent(
                targetState = uiState,
                label = "VerificationUiTransition"
            ) { state ->
                when (state) {
                    is VerificationUiState.Idle -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Waiting for input to verify.",
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 13.sp
                            )
                        }
                    }
                    is VerificationUiState.Loading -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "AI analyzing suspicious markers...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    "Evaluating domain registrar record & fee red flags",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                    is VerificationUiState.Success -> {
                        val report = state.result
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verifier_result_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Analysis Results",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(onClick = { viewModel.resetVerification() }) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Reset analysis")
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Visual verification score card
                                val scoreColor = when {
                                    report.verificationScore >= 80 -> Color(0xFF137333)
                                    report.verificationScore >= 50 -> Color(0xFFB45309)
                                    else -> Color(0xFFC5221F)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(scoreColor.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .background(scoreColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${report.verificationScore}%",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = "Verification Score",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        Text(
                                            text = "Trust status indicates a ${report.trustLevel} level of assurance.",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = scoreColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Detected Flags
                                if (report.flags.isNotEmpty()) {
                                    Text(
                                        text = "DETECTED SYSTEM MARKERS & FLAGS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    report.flags.forEach { flag ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = Color(0xFFD97706),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = flag,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Official Source Check
                                Row {
                                    Text(
                                        text = "Official Source Check: ",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = report.officialSource.ifBlank { "Not Found/Suspect" },
                                        fontSize = 13.sp,
                                        color = if (report.officialSource.isNotBlank()) Color(0xFF137333) else Color(0xFFC5221F),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Deep analysis text block
                                Text(
                                    text = "AI Trust Explanation Analyst:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = report.analysis,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Recommended Steps
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "SAFETY RECOMMENDATION",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = report.recommendation,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is VerificationUiState.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, "Error", tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Error: ${(state as VerificationUiState.Error).message}",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- AI COUNSELOR CHAT SCREEN ---
@Composable
fun CounselorTabScreen(viewModel: SponsorshipViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val chatUiState by viewModel.chatUiState.collectAsStateWithLifecycle()
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("counselor_screen")
    ) {
        // Chat Header bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 4.dp,
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("AI Intelligent Counselor", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Online • Personalized guidance", fontSize = 11.sp, color = Color(0xFF137333))
                    }
                }
                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.testTag("clear_chat_btn")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Chat history", tint = MaterialTheme.colorScheme.outline)
                }
            }
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Column(
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Surface(
                            shape = if (isUser) {
                                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
                            } else {
                                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
                            },
                            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = if (isUser) 0.dp else 1.dp
                        ) {
                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                fontSize = 14.sp,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            if (chatUiState is ChatUiState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Counselor typing...", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }

        // Send Input Bar
        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Ask about deadlines, qualifications, letters...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendChatMessage(messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("send_msg_btn"),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send message",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


// --- PORTAL & STUDENT CENTER TAB ---
@Composable
fun PortalTabScreen(viewModel: SponsorshipViewModel, context: Context) {
    val studentProfile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val appliedList by viewModel.appliedScholarships.collectAsStateWithLifecycle()

    var nameVal by remember { mutableStateOf("") }
    var emailVal by remember { mutableStateOf("") }
    var fieldVal by remember { mutableStateOf("") }
    var educationVal by remember { mutableStateOf("University") }
    var needsVal by remember { mutableStateOf("") }
    var continentVal by remember { mutableStateOf("") }
    var countryVal by remember { mutableStateOf("") }
    var stateProvinceVal by remember { mutableStateOf("") }

    val educationLevels = listOf("Secondary School", "College", "University", "Technical/Vocational")

    // Image Picker for Profile Picture
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateProfilePicture(it.toString())
        }
    }

    // PDF Document Picker for Resume/CV
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = getFileName(context, it) ?: "Academic_Transcript_CV.pdf"
            viewModel.updatePdfDocument(it.toString(), name)
        }
    }

    LaunchedEffect(studentProfile) {
        studentProfile?.let {
            nameVal = it.fullName
            emailVal = it.email
            fieldVal = it.fieldOfStudy
            educationVal = it.educationLevel.ifEmpty { "University" }
            needsVal = it.financialNeed
            continentVal = it.continent
            countryVal = it.country
            stateProvinceVal = it.stateProvince
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("portal_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome portal header with circular avatar loaded dynamically via Coil
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (studentProfile?.profileImageUri != null) {
                            Image(
                                painter = coil.compose.rememberAsyncImagePainter(studentProfile?.profileImageUri),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        val isReg = studentProfile?.isRegistered == true
                        Text(
                            text = if (isReg) "Welcome, ${studentProfile?.fullName}" else "Student Portal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (isReg) Color(0xFF137333) else Color(0xFFC5221F), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isReg) "Verified Matching Profile Connected" else "No matching profile completed list",
                                fontSize = 12.sp,
                                color = if (isReg) Color(0xFF137333) else Color(0xFFC5221F)
                            )
                        }
                    }
                }
            }
        }

        // Complete Student Registration Details Form
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Candidate Matching Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Complete details to automatically match your field of study & details with trusted active bursary programs.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Image Editor Selector Section
                    Text(
                        text = "Profile Picture",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (studentProfile?.profileImageUri != null) {
                                Image(
                                    painter = coil.compose.rememberAsyncImagePainter(studentProfile?.profileImageUri),
                                    contentDescription = "Edit Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Edit Profile Picture",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("upload_pic_btn"),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Image", fontSize = 11.sp)
                            }
                            
                            OutlinedButton(
                                onClick = {
                                    val sampleAvatars = listOf(
                                        "https://images.unsplash.com/photo-1544005313-94ddf0286df2?q=80&w=200&auto=format&fit=crop",
                                        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?q=80&w=200&auto=format&fit=crop",
                                        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200&auto=format&fit=crop"
                                    )
                                    viewModel.updateProfilePicture(sampleAvatars.random())
                                    Toast.makeText(context, "Sample profile avatar applied!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sample Avatar", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Full Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = nameVal,
                        onValueChange = { nameVal = it },
                        modifier = Modifier.fillMaxWidth().testTag("profile_name_field"),
                        placeholder = { Text("e.g. Marie Curie") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Email Address", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = emailVal,
                        onValueChange = { emailVal = it },
                        modifier = Modifier.fillMaxWidth().testTag("profile_email_field"),
                        placeholder = { Text("e.g. marie@gmail.com") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Field of Study", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = fieldVal,
                        onValueChange = { fieldVal = it },
                        modifier = Modifier.fillMaxWidth().testTag("profile_field_of_study"),
                        placeholder = { Text("e.g. Computer Science, Public Health") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Continent / Region", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = continentVal,
                        onValueChange = { continentVal = it },
                        modifier = Modifier.fillMaxWidth().testTag("profile_continent_field"),
                        placeholder = { Text("e.g. Africa, North America, Asia, Europe") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Quick-selection chips for continents
                    val continentsList = listOf("Africa", "Asia", "Europe", "North America", "South America", "Oceania")
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(continentsList) { cont ->
                            val isSelected = continentVal.equals(cont, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.clickable { continentVal = cont }
                            ) {
                                Text(
                                    text = cont,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Country", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(
                                value = countryVal,
                                onValueChange = { countryVal = it },
                                modifier = Modifier.testTag("profile_country_field"),
                                placeholder = { Text("e.g. Zambia, USA, India") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("State / Province", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                            OutlinedTextField(
                                value = stateProvinceVal,
                                onValueChange = { stateProvinceVal = it },
                                modifier = Modifier.testTag("profile_state_field"),
                                placeholder = { Text("e.g. Lusaka, Texas, Ontario") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Education Level", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        educationLevels.forEach { level ->
                            val isSelected = educationVal == level
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { educationVal = level }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = level,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Narrative of Financial Need", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = needsVal,
                        onValueChange = { needsVal = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("profile_narrative_field"),
                        placeholder = { Text("Describe details of financial hardships or sponsorship requirements...") },
                        maxLines = 5,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Document/PDF Upload Section
                    Text(
                        text = "CV / Academic Transcripts (PDF)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "PDF document icon",
                                    tint = if (studentProfile?.pdfDocumentUri != null) Color(0xFFC5221F) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = studentProfile?.pdfDocumentName ?: "No academic document attached",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (studentProfile?.pdfDocumentUri != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (studentProfile?.pdfDocumentUri != null) "Verified PDF Saved Locally" else "Required by top bursaries",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                        
                        if (studentProfile?.pdfDocumentUri != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    viewModel.updatePdfDocument(null, null)
                                    Toast.makeText(context, "Document detached.", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove document",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { pdfPickerLauncher.launch("application/pdf") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.1f).testTag("upload_pdf_btn"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pick PDF File", fontSize = 11.sp, maxLines = 1)
                        }
                        
                        OutlinedButton(
                            onClick = {
                                viewModel.updatePdfDocument("content://simulated/academic_cv_transcript.pdf", "Marie_Curie_Official_Transcript_and_CV.pdf")
                                Toast.makeText(context, "Simulated Academic Transcript PDF attached!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fast Demo CV", fontSize = 11.sp, maxLines = 1)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (nameVal.isBlank() || emailVal.isBlank()) {
                                Toast.makeText(context, "Full Name & Email Address are required to complete profile!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.submitProfile(
                                    name = nameVal,
                                    email = emailVal,
                                    field = fieldVal,
                                    education = educationVal,
                                    needs = needsVal,
                                    continent = continentVal,
                                    country = countryVal,
                                    stateProvince = stateProvinceVal
                                )
                                Toast.makeText(context, "Profile successfully saved to device!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_profile_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Register / Save Local Profile")
                    }
                }
            }
        }

        // Live Applications database Tracker list
        item {
            Text(
                text = "My Active Sponsorship Tracker",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (appliedList.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "You haven't initiated any applications yet. Go to Explore and click Apply Now on any verified program.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(appliedList) { app ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = app.scholarshipName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.withdrawApplication(app.scholarshipId) }) {
                                Icon(Icons.Default.Delete, "Cancel application", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Progress Tracker Steps
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFFE2E8F0), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Candidate Profile Verified",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Status: Verification Pending",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
            }
        }
    }
}
