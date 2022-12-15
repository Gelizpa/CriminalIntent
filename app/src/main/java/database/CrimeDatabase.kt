import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.criminalintent.Crime

@Database(entities = [ Crime::class ],
    version=1)
@TypeConverters(CrimeTypeConverters::class)

abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao(): CrimeDao//Теперь при создании базы данных Room будет генерировать конкретную реализацию в DAO, и вы можете получить к ней доступ.

}