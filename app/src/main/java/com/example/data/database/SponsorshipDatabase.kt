package com.example.data.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "student_profile")
data class StudentProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val email: String = "",
    val fieldOfStudy: String = "",
    val educationLevel: String = "",
    val financialNeed: String = "",
    val isRegistered: Boolean = false,
    val profileImageUri: String? = null,
    val pdfDocumentUri: String? = null,
    val pdfDocumentName: String? = null,
    val continent: String = "",
    val country: String = "",
    val stateProvince: String = ""
)

@Entity(tableName = "applied_scholarships")
data class AppliedScholarship(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scholarshipId: Int,
    val scholarshipName: String,
    val appliedDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarked_scholarships")
data class BookmarkedScholarship(
    @PrimaryKey val scholarshipId: Int
)

@Dao
interface SponsorshipDao {
    @Query("SELECT * FROM student_profile WHERE id = 1")
    fun getProfileFlow(): Flow<StudentProfile?>

    @Query("SELECT * FROM student_profile WHERE id = 1")
    suspend fun getProfile(): StudentProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: StudentProfile)

    @Query("SELECT * FROM applied_scholarships ORDER BY appliedDate DESC")
    fun getAppliedScholarshipsFlow(): Flow<List<AppliedScholarship>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppliedScholarship(applied: AppliedScholarship)

    @Query("DELETE FROM applied_scholarships WHERE scholarshipId = :scholarshipId")
    suspend fun deleteAppliedScholarship(scholarshipId: Int)

    @Query("SELECT * FROM bookmarked_scholarships")
    fun getBookmarksFlow(): Flow<List<BookmarkedScholarship>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkedScholarship)

    @Query("DELETE FROM bookmarked_scholarships WHERE scholarshipId = :scholarshipId")
    suspend fun removeBookmark(scholarshipId: Int)
}

@Database(
    entities = [StudentProfile::class, AppliedScholarship::class, BookmarkedScholarship::class],
    version = 3,
    exportSchema = false
)
abstract class SponsorshipDatabase : RoomDatabase() {
    abstract fun sponsorshipDao(): SponsorshipDao

    companion object {
        @Volatile
        private var INSTANCE: SponsorshipDatabase? = null

        fun getDatabase(context: Context): SponsorshipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SponsorshipDatabase::class.java,
                    "sponsorship_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class SponsorshipRepository(private val dao: SponsorshipDao) {
    val studentProfile: Flow<StudentProfile?> = dao.getProfileFlow()
    val appliedScholarships: Flow<List<AppliedScholarship>> = dao.getAppliedScholarshipsFlow()
    val bookmarks: Flow<List<BookmarkedScholarship>> = dao.getBookmarksFlow()

    suspend fun getProfileDirect(): StudentProfile? = dao.getProfile()

    suspend fun saveProfile(profile: StudentProfile) {
        dao.saveProfile(profile)
    }

    suspend fun applyForScholarship(scholarshipId: Int, name: String) {
        dao.insertAppliedScholarship(
            AppliedScholarship(
                scholarshipId = scholarshipId,
                scholarshipName = name
            )
        )
    }

    suspend fun cancelApplication(scholarshipId: Int) {
        dao.deleteAppliedScholarship(scholarshipId)
    }

    suspend fun toggleBookmark(scholarshipId: Int, isBookmarked: Boolean) {
        if (isBookmarked) {
            dao.addBookmark(BookmarkedScholarship(scholarshipId))
        } else {
            dao.removeBookmark(scholarshipId)
        }
    }
}
