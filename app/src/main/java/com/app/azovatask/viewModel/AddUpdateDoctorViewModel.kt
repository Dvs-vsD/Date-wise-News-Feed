package com.app.azovatask.viewModel

import androidx.lifecycle.ViewModel
import com.app.azovatask.model.DoctorDetailsRealmModel
import com.app.azovatask.repository.Repository

class AddUpdateDoctorViewModel: ViewModel() {

    private var repository: Repository = Repository()

    fun getDoctorRow(id: Int): DoctorDetailsRealmModel {
        return repository.getDoctorRow(id)
    }

    fun updateDoctor(id: Int, docName: String, specialization: String) {
        repository.updateDoctor(id, docName, specialization)
    }

    fun addDoctor(docName: String, specialization: String) {
        repository.addDoctor(docName, specialization)
    }
}