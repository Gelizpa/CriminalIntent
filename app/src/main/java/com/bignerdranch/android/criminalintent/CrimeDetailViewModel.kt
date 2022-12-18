package com.bignerdranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class CrimeDetailViewModel() : ViewModel() {
    private val crimeRepository = CrimeRepository.get()//В свойстве CrimeRepository хранится связь с CrimeRepository
    private val crimeIdLiveData = MutableLiveData<UUID>()//CrimeIdLiveData хранит идентификатор отображаемого в данный момент преступления (или выводимого на отображение) фрагментом CrimeFragment
    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) {
                crimeId -> crimeRepository.getCrime(crimeId)
        }//Функция преобразования возвращает новый объект LiveData, который мы называем результатом преобразования, значение которого обновляется каждый раз, когда изменяется значение триггерного объекта LiveData.

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }//Свойство value объекта LiveData, возвращаемое из функции отображения, используется для установки свойства value для результата преобразования.
    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)//CrimeRepository обрабатывает запрос на обновление в фоновом потоке, интеграция с базой данных реализуется просто.
    }//Функция saveCrime(Crime) принимает объект Crime и записывает его в базу данных.
    fun getPhotoFile(crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }

}