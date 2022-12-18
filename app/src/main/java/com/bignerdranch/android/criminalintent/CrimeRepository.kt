package com.bignerdranch.android.criminalintent

import androidx.room.Room
import database.CrimeDatabase
import android.content.Context
import androidx.lifecycle.LiveData
import database.migration_1_2
import java.io.File
import java.util.*
import java.util.concurrent.Executors


private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: android.content.Context) {


    private val database : CrimeDatabase = Room.databaseBuilder(context.applicationContext,
        CrimeDatabase::class.java, DATABASE_NAME).addMigrations(migration_1_2).build()
    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()//Функция newSingleThreadExecutor() возвращает экземпляр исполнителя, который указывает на новый поток.
    //Таким образом, любая работа, которую вы выполняете сисполнителем, будет происходить вне основного потока.
    private val filesDir = context.applicationContext.filesDir
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)
    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }// execute{}. Он выталкивает эти операции из основного потока, чтобы не блокировать работу  пользовательского интерфейса.

    }
    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }
    fun getPhotoFile(crime: Crime): File = File(filesDir, crime.photoFileName)

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }//Функция запроса будет работать не очень хорошо, если не вызвать перед ней функцию Initialize(). Она выбросит исключение IllegalStateException
        }
        fun get(): CrimeRepository {
            return INSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }//CrimeRepository — это одноэлементный класс (синглтон).Это означает, что в вашем процессе приложения единовременно существует только один его экземпляр.
}


