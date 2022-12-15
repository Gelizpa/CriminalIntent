package database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bignerdranch.android.criminalintent.Crime
import java.util.*

@Dao//сообщает Room, что CrimeDao — это один из ваших объектов доступа к данным.
interface CrimeDao {
    @Query("SELECT * FROM crime")//указывает, что getCrimes() и getCrime(UUID) предназначены для извлечения информации из базы данных, а не вставки, обновления или удаления элементов из базы данных
    //SELECT*FROMcrime выводит все столбцы для всех строк таблицы crime
    fun getCrimes(): LiveData<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")// Команда SELECTFROMcrimeWHEREid=(:id) запрашивает все столбцы только из строки с нужным  идентификатором.

    fun getCrime(id: UUID): LiveData<Crime?>//Возвращая экземпляр LiveData из вашего класса DAO, вызапускаете запрос в фоновом потоке. Когда запрос завершается,объект LiveData будет обрабатывать отправку данных преступлений в основной поток и сообщать о любых наблюдателях

}

