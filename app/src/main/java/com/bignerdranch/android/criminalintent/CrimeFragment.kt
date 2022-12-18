
import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore

import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.util.*
import androidx.lifecycle.Observer
import com.bignerdranch.android.criminalintent.*
import java.io.File

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0//константа для кода запроса
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DIALOG_PICTURE = "DialogePicture"
class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {
    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }//Запрос у ViewModel загрузку Crime в onCreate(...).



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button



        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    photoFile =
                        crimeDetailViewModel.getPhotoFile(crime)
                    photoUri =
                        FileProvider.getUriForFile(requireActivity(),
                            "com.bignerdranch.android.criminalintent.fileprovider",
                                    photoFile)
                    updateUI()
                }
            })
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher
        {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
// Это пространство оставлено пустым специально
            }
            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }
            override fun
                    afterTextChanged(sequence: Editable?) {
// И это
            }
        }
        titleField.addTextChangedListener(titleWatcher)
        solvedCheckBox.apply { setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked
            }
            dateButton.setOnClickListener {
                DatePickerFragment.newInstance(crime.date).apply {
                    setTargetFragment(this@CrimeFragment, REQUEST_DATE)//Функция получает фрагмент, который станет целевым, и код запроса(CrimeFragment целевой фрагмент экземпляра DatePickerFragment)
                    show(this@CrimeFragment
                        .requireFragmentManager(), DIALOG_DATE)
                }
            }
            reportButton.setOnClickListener {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT,
                        getCrimeReport())
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject))
                }.also { intent ->
                    val chooserIntent =
                        Intent.createChooser(intent,
                            getString(R.string.send_report))
                    startActivity(chooserIntent)
                }//Здесь мы используем конструктор Intent, который получает строку с константой, описывающей действие
            }
            suspectButton.apply {
                val pickContactIntent =
                    Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI)
                setOnClickListener {
                    startActivityForResult(pickContactIntent, REQUEST_CONTACT)
                }



            }
            photoButton.apply {


                setOnClickListener {
                    Log.d (TAG,"Photo File $photoUri")
                    //startActivityForResult(captureImage, REQUEST_PHOTO)
                    takePicture.launch(photoUri)
                }
            }



        }}
        private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
                success: Boolean ->
            if (success) {

                Log.d (TAG, "We took a picture...")
                updatePhotoView()
            }
    }
    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }//Функция Fragment.onStop() вызывается всякий раз, когда ваш фрагмент переходит в состояние остановки. Это означает, что данные будут сохранены, когда пользователь закроет экран подробностей (например, нажав кнопку «Назад»).
    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }// Реализация интерфейса обратных вызовов


    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
        updatePhotoView()
    }
    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap =
                getScaledBitmap(photoFile.path,
                    requireActivity())
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageDrawable(null)
        }
    }
    override fun onActivityResult(requestCode:
                                  Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK ->
                return
            requestCode == REQUEST_CONTACT &&
                    data != null -> {
                val contactUri: Uri? = data.data
// Указать, для каких полей ваш запрос должен возвращать значения.
                val queryFields =
                    arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
// Выполняемый здесь запрос — contactUri похож на предложение "where"
                val cursor =
                    requireActivity().contentResolver
                        .query(contactUri!!,queryFields, null, null, null)
                cursor?.use { // Verify cursor contains at least one result
                     if (it.count == 0) {
                                return
                            }
// Первый столбец первой строки данных —
// это имя вашего подозреваемого.
                    it.moveToFirst()
                    val suspect =
                        it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text =
                        suspect
                }
            }
            requestCode == REQUEST_PHOTO -> {requireActivity().revokeUriPermission(photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                updatePhotoView()
            }
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved)
        {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString =
            DateFormat.format(DATE_FORMAT,
                crime.date).toString()
        var suspect = if
                              (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report,
            crime.title, dateString,
            solvedString, suspect)
    }//функция, которая создает четыре строки, соединяет их и возвращает полный отчет

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply { arguments = args
            }
        }
    }//получает UUID, создает пакет аргументов, создает экземпляр фрагмента, а затем присоединяет аргументы к фрагменту

}
