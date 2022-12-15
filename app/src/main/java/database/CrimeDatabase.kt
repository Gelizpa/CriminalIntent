import androidx.room.Database
import androidx.room.RoomDatabase
import com.bignerdranch.android.criminalintent.Crime

@Database(entities = [ Crime::class ],
    version=1)
abstract class CrimeDatabase : RoomDatabase() {
}