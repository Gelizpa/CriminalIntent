package com.bignerdranch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*
private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {
    override fun
            onCreateDialog(savedInstanceState: Bundle?):
            Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date

        val initialYear =
            calendar.get(Calendar.YEAR)
        val initialMonth =
            calendar.get(Calendar.MONTH)
        val initialDay =
            calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(),//контекстный объект, который необходим для доступа к необходимым ресурсам элемента
            null,//слушатель дат
            initialYear,//это год
            initialMonth,//месяц
            initialDay// день,к которым должно быть инициализировано окно выбора даты.
        )
    }
    companion object {
        fun newInstance(date: Date):
                DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }// функция newInstance Чтобы передать дату преступления в DatePickerFragment
    }

}