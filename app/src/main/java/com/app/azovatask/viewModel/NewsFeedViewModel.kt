package com.app.azovatask.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.azovatask.model.NewsDataModel
import com.app.azovatask.repository.Repository
import java.util.*
import kotlin.collections.ArrayList

class NewsFeedViewModel: ViewModel() {

    private var repository = Repository()

    fun getNewsFeeds(): LiveData<ArrayList<NewsDataModel>> {
        return repository.getNewsFeeds()
    }

    fun getDateWiseNews(today: Date, tomorrow: Date) {
        repository.getDateWiseNews(today, tomorrow)
    }

    fun searchNews(today: Date, tomorrow: Date,str: String) {
        repository.searchNews(today, tomorrow, str)
    }
}