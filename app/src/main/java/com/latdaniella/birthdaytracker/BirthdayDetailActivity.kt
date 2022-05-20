package com.latdaniella.birthdaytracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.latdaniella.birthdaytracker.databinding.ActivityBirthdayDetailBinding
import java.text.SimpleDateFormat
import java.util.*


class BirthdayDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityBirthdayDetailBinding
    var personIsEditable = false
    var cal = Calendar.getInstance()
    lateinit var person: Person

    companion object {
        val EXTRA_PERSON = "person"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirthdayDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        person = intent.getParcelableExtra<Person>(EXTRA_PERSON) ?: Person()
        if(person == null){
            person = Person()
        }

        binding.editTextBirthdayDetailName.setText(person.name)
        binding.editTextBirthdayDetailDesiredGift.setText(person.desiredGift)
        binding.editTextBirthdayDetailBudget.setText(person.budget.toString())
        binding.checkBoxBirthdayDetailGiftBought.isChecked = person.isPurchased
        binding.textViewBirthdayDetailBirthdate.text = person.birthday.toString()

        binding.buttonBirthdayDetailSave.setOnClickListener {
            if(person == null){
                person = Person()
            }
            person!!.name = binding.editTextBirthdayDetailName.text.toString()
            person!!.budget = binding. editTextBirthdayDetailBudget.text.toString().toInt()

            Backendless.Data.of(Person::class.java).save(person, object : AsyncCallback<Person?> {
                override fun handleResponse(response: Person?) {
                    Toast.makeText(this@BirthdayDetailActivity, "save successful", Toast.LENGTH_SHORT).show()
                }

                override fun handleFault(fault: BackendlessFault) {
                    Log.d("BirthdayActivity", "handleFault: ${fault.message}")
                }
            })
        }

        // datepicker code from https://www.tutorialkart.com/kotlin-android/android-datepicker-kotlin-example/
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        binding.textViewBirthdayDetailBirthdate.setOnClickListener {
            if(personIsEditable) {
                DatePickerDialog(this, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.textViewBirthdayDetailBirthdate.text = sdf.format(cal.getTime())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_birthday_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_item_birthday_detail_edit -> {
                toggleEditable()
                true
            }
            R.id.menu_item_birthday_detail_delete -> {
                deleteFromBackendless()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun deleteFromBackendless() {
        Backendless.Data.of(Person::class.java).remove(person, object : AsyncCallback<Long?> {
                override fun handleResponse(response: Long?) {
                    // Contact has been deleted. The response is the
                    // time in milliseconds when the object was deleted
                    Toast.makeText(this@BirthdayDetailActivity, "delete successful", Toast.LENGTH_SHORT).show()
                }

                override fun handleFault(fault: BackendlessFault) {
                    // an error has occurred, the error code can be
                    // retrieved with fault.getCode()
                    Toast.makeText(this@BirthdayDetailActivity, "delete failed", Toast.LENGTH_SHORT).show()
                }
            })
        }

    private fun toggleEditable() {
        if (personIsEditable) {
            personIsEditable = false
            binding.buttonBirthdayDetailSave.isEnabled = false
            binding.buttonBirthdayDetailSave.visibility = View.GONE
            binding.checkBoxBirthdayDetailGiftBought.isEnabled = false
            binding.editTextBirthdayDetailName.inputType = InputType.TYPE_NULL
            binding.editTextBirthdayDetailName.isEnabled = false
            binding.editTextBirthdayDetailDesiredGift.inputType = InputType.TYPE_NULL
            binding.editTextBirthdayDetailDesiredGift.isEnabled = false
            binding.editTextBirthdayDetailBudget.inputType = InputType.TYPE_NULL
            binding.editTextBirthdayDetailBudget.isEnabled = false
            binding.textViewBirthdayDetailBirthdate.isClickable = false
        } else {
            personIsEditable = true
            binding.buttonBirthdayDetailSave.isEnabled = true
            binding.buttonBirthdayDetailSave.visibility = View.VISIBLE
            binding.checkBoxBirthdayDetailGiftBought.isEnabled = true
            binding.editTextBirthdayDetailName.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            binding.editTextBirthdayDetailName.isEnabled = true
            binding.editTextBirthdayDetailDesiredGift.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            binding.editTextBirthdayDetailDesiredGift.isEnabled = true
            binding.editTextBirthdayDetailBudget.inputType = InputType.TYPE_NUMBER_VARIATION_NORMAL
            binding.editTextBirthdayDetailBudget.isEnabled = true
            binding.textViewBirthdayDetailBirthdate.isClickable = true
        }
    }

}