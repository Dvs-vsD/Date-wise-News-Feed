package com.app.azovatask.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.azovatask.databinding.ActivityAddDoctorBinding
import com.app.azovatask.viewModel.AddUpdateDoctorViewModel

class ActivityAddDoctor : AppCompatActivity() {

    private lateinit var binding: ActivityAddDoctorBinding
    private var isUpdate: Boolean = false
    private var rowId: Int? = 0
    private var docName: String? = ""
    private var docSpecialization: String? = ""
    private lateinit var addUpdateDoctorViewModel: AddUpdateDoctorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDoctorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        addUpdateDoctorViewModel = ViewModelProvider(this).get(AddUpdateDoctorViewModel::class.java)

        binding.ivBack.setOnClickListener { onBackPressed() }

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        isUpdate = intent.getBooleanExtra("isUpdate", false)
        rowId = intent.getIntExtra("id", 0)

        if (isUpdate) {
            binding.tvAddDoctor.text = "Update Doctor"
            binding.btnAdd.text = "Update"
            val row = addUpdateDoctorViewModel.getDoctorRow(rowId!!)
            binding.etDoctorName.setText(row.docName)
            binding.etDoctorSpecialization.setText(row.specialization)
        }

        binding.btnAdd.setOnClickListener {
            docName = binding.etDoctorName.text?.trim().toString()
            docSpecialization = binding.etDoctorSpecialization.text?.trim().toString()

            if (docName?.isEmpty() == true) {
                binding.etDoctorName.error = "Please Enter Doctor Name"
                binding.etDoctorName.requestFocus()
            } else if (docSpecialization?.isEmpty()?:false) {
                binding.etDoctorSpecialization.error = "Please Enter Doctor's Specialization"
                binding.etDoctorSpecialization.requestFocus()
            } else {
                if (isUpdate) {
                    addUpdateDoctorViewModel.updateDoctor(rowId!!, docName!!, docSpecialization!!)
                    Toast.makeText(this, "Entry Updated", Toast.LENGTH_SHORT).show()
                } else {
                    addUpdateDoctorViewModel.addDoctor(docName?:"-", docSpecialization?:"-")
                    Toast.makeText(this, "Entry Inserted", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }
}