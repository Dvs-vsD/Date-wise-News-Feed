package com.app.azovatask.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.azovatask.model.DoctorDetailsRealmModel
import com.app.azovatask.model.NewsDataModel
import com.app.azovatask.model.NewsModel
import com.app.azovatask.network.RetrofitClient
import com.app.azovatask.utils.Utils
import com.app.azovatask.utils.Utils.formatTo
import com.app.azovatask.utils.Utils.toDate
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class Repository {

    private var doctorList: MutableLiveData<ArrayList<DoctorDetailsRealmModel>> =
        MutableLiveData(ArrayList())
    private var newsFeeds: MutableLiveData<ArrayList<NewsDataModel>> =
        MutableLiveData(ArrayList())
    private var totalItemCount: MutableLiveData<Int> = MutableLiveData(0)
    private val mRealmResults: RealmResults<DoctorDetailsRealmModel>
    private var newsFeedResults: RealmResults<NewsDataModel>
    private val mRealm: Realm = Realm.getDefaultInstance()
    private var url: String

    init {
        mRealmResults = mRealm.where(DoctorDetailsRealmModel::class.java).findAll()
        doctorList.value?.addAll(mRealmResults)

        mRealmResults.addChangeListener { change ->
            doctorList.value?.clear()
            val changeList = ArrayList<DoctorDetailsRealmModel>()
            changeList.addAll(change)
            doctorList.value = changeList
            Timber.e("changes %s", doctorList.value.toString())
        }
    }

    fun getDoctorList(): LiveData<ArrayList<DoctorDetailsRealmModel>> {
        return doctorList
    }

    fun getDoctorRow(id: Int): DoctorDetailsRealmModel {
        val row = mRealm.where(DoctorDetailsRealmModel::class.java).equalTo("id", id).findFirst()
        return row!!
    }

    fun selectAll() {
        for (mDoctor in doctorList.value!!) {
            if (!mDoctor.checkedStatus) {
                mDoctor.checkedStatus = true
                totalItemCount.value = totalItemCount.value?.plus(1)
            }
        }
    }

    fun deselectAll() {
        for (mDoctor in doctorList.value!!) {
            mDoctor.checkedStatus = false
            totalItemCount.value = 0
        }
    }

    fun getTotalItemCount(): LiveData<Int> {
        return totalItemCount
    }

    fun addCount(count: Int) {
        totalItemCount.value = totalItemCount.value?.plus(count)
    }

    fun removeSelectedRows() {
        mRealm.executeTransaction {
            for (mDoctor in doctorList.value!!) {
                if (mDoctor.checkedStatus) {
                    val row =
                        mRealm.where(DoctorDetailsRealmModel::class.java)
                            .equalTo("id", mDoctor.id)
                            .findFirst()
                    row?.deleteFromRealm()
                }
            }
        }
        totalItemCount.value = 0
    }

    fun updateDoctor(id: Int, docName: String, specialization: String) {
        mRealm.executeTransaction {
            val row =
                mRealm.where(DoctorDetailsRealmModel::class.java).equalTo("id", id).findFirst()
            row?.docName = docName
            row?.specialization = specialization
            mRealm.copyToRealmOrUpdate(row!!)
        }
        totalItemCount.value = 0
    }

    fun addDoctor(docName: String, specialization: String) {
        mRealm.executeTransaction {
            val id = mRealm.where(DoctorDetailsRealmModel::class.java).max("id")

            val nextId: Int = if (id == null)
                1
            else
                id.toInt() + 1

            val doctorObj = DoctorDetailsRealmModel(nextId, docName, specialization, false)
            mRealm.insert(doctorObj)
        }
    }

    fun searchDoctor(str: String) {
        val searchList = mRealm.where(DoctorDetailsRealmModel::class.java)
            .contains("docName", str, Case.INSENSITIVE).findAll()
        if (searchList.isNotEmpty()) {
            doctorList.value?.clear()
            val list = ArrayList<DoctorDetailsRealmModel>()
            list.addAll(searchList)
            doctorList.value = list
        }
    }

    /**
     * News Feed data is managed from below
     */

    init {
        val today = Utils.todayDate(Date())
        val tomorrow = Utils.tomorrowDate(Date())

        val todayDate: String = today.formatTo("yyyy-MM-dd")

        url =
            "v2/everything?q=tesla&from=$todayDate&sortBy=publishedAt&apiKey=69f6a9be31444e6f83e87cf28bc53366"

        fetchNewsFeeds()

        newsFeedResults = mRealm.where(NewsDataModel::class.java).greaterThanOrEqualTo(
            "pub_date", today
        ).lessThanOrEqualTo("pub_date", tomorrow)
            .findAll()
        newsFeeds.value?.addAll(newsFeedResults)
        Timber.e(newsFeeds.toString())

        newsFeedResults.addChangeListener { change ->
            newsFeeds.value?.clear()
            val changeList = ArrayList<NewsDataModel>()
            changeList.addAll(change)
            newsFeeds.value = changeList
        }
    }

    fun getNewsFeeds(): LiveData<ArrayList<NewsDataModel>> {
        return newsFeeds
    }

    fun getDateWiseNews(today: Date, tomorrow: Date) {
        val list = ArrayList<NewsDataModel>()
        list.addAll(
            mRealm.where(NewsDataModel::class.java)
                .greaterThanOrEqualTo("pub_date", today)
                .lessThanOrEqualTo("pub_date", tomorrow)
                .findAll()
        )
        newsFeeds.value = list
    }

    fun searchNews(today: Date, tomorrow: Date, str: String) {
        val searchResults = mRealm.where(NewsDataModel::class.java)
            .greaterThanOrEqualTo("pub_date", today)
            .lessThanOrEqualTo("pub_date", tomorrow)
            .contains("header", str, Case.INSENSITIVE).findAll()
        if (searchResults.isNotEmpty()) {
            newsFeeds.value?.clear()
            val list = ArrayList<NewsDataModel>()
            list.addAll(searchResults)
            newsFeeds.value = list
        }
    }

    private fun fetchNewsFeeds() {
        val call = RetrofitClient.getClient().getNewsFeed(url)
        call.enqueue(object : Callback<NewsModel> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<NewsModel>, response: Response<NewsModel>) {
                if (response.body() != null) {
                    if (response.body()!!.status == "ok") {
                        val data = response.body()!!.articles
                        Timber.e(data.size.toString())
                        val list = ArrayList<NewsDataModel>()
                        for (index in data.indices) {
                            val date = data[index].publishedAt?.toDate("yyyy-MM-dd'T'HH:mm:ss'Z'")
                            Timber.e(date.toString())
                            val news = data[index]
                            val mModel = NewsDataModel()
                            mModel.header = news.header ?: ""
                            mModel.author = news.author ?: ""
                            mModel.description = news.description ?: ""
                            mModel.urlToImage = news.urlToImage ?: ""
                            mModel.publishedAt = news.publishedAt ?: ""
                            mModel.pub_date = date

                            list.add(mModel)
                        }
                        mRealm.executeTransaction {
                            mRealm.insertOrUpdate(list)
                        }
                        Timber.e(list.toString())
                    }
                } else {
                    Timber.e("Something went wrong while fetching news feeds")
                }
            }

            override fun onFailure(call: Call<NewsModel>, t: Throwable) {
                Timber.e(t.message.toString())
            }
        })
    }
}