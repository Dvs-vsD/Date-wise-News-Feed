package com.app.azovatask.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.azovatask.model.DoctorDetailsRealmModel
import com.app.azovatask.repository.Repository

class MainViewModel : ViewModel() {

    private var repository: Repository = Repository()

    fun getDoctorList(): LiveData<ArrayList<DoctorDetailsRealmModel>> {
        return repository.getDoctorList()
    }

    fun selectAll() {
        repository.selectAll()
    }

    fun deselectAll() {
        repository.deselectAll()
    }

    fun getTotalItemCount(): LiveData<Int> {
        return repository.getTotalItemCount()
    }

    fun addCount(count: Int) {
        repository.addCount(count)
    }

    fun removeSelectedRows() {
        repository.removeSelectedRows()
    }

    fun search(str: String) {
        repository.searchDoctor(str)
    }
}