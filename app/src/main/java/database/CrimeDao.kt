import androidx.room.Dao
import androidx.room.Query
import com.bignerdranch.android.criminalintent.Crime
import java.util.*

@Dao//сообщает Room, что CrimeDao — это один из ваших объектов доступа к данным.
interface CrimeDao {
    @Query("SELECT * FROM crime")//указывает, что getCrimes() и getCrime(UUID) предназначены для извлечения информации из базы данных, а не вставки, обновления или удаления элементов из базы данных
    //SELECT*FROMcrime выводит все столбцы для всех строк таблицы crime
    fun getCrimes(): List<Crime>
    @Query("SELECT * FROM crime WHERE id=(:id)")// Команда SELECTFROMcrimeWHEREid=(:id) запрашивает все столбцы только из строки с нужным  идентификатором.

    fun getCrime(id: UUID): Crime?
}

