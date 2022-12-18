package database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.criminalintent.Crime
import java.util.*

@Dao//сообщает Room, что CrimeDao — это один из ваших объектов доступа к данным.
interface CrimeDao {
    @Query("SELECT * FROM crime")//указывает, что getCrimes() и getCrime(UUID) предназначены для извлечения информации из базы данных, а не вставки, обновления или удаления элементов из базы данных
    //SELECT*FROMcrime выводит все столбцы для всех строк таблицы crime
    fun getCrimes(): LiveData<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")// Команда SELECTFROMcrimeWHEREid=(:id) запрашивает все столбцы только из строки с нужным  идентификатором.

    fun getCrime(id: UUID): LiveData<Crime?>//Возвращая экземпляр LiveData из вашего класса DAO, вызапускаете запрос в фоновом потоке. Когда запрос завершается,объект LiveData будет обрабатывать отправку данных преступлений в основной поток и сообщать о любых наблюдателях
    @Update
    fun updateCrime(crime: Crime)//В функции updateCrime() используется аннотация @Update. Эта функция принимает объект преступления, используя идентификатор, сохраненный в этом преступлении, чтобы найти соответствующую строку, а затем обновляет данные в этой строке, основываясь на новых данных в объекте преступления
    @Insert
    fun addCrime(crime: Crime)//В функции addCrime() используется аннотация @Insert. Параметр — это преступление, которое вы хотите добавить в таблицу базы данных
}

