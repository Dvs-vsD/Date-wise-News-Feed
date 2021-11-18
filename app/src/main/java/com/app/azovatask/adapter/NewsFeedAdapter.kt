package com.app.azovatask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.azovatask.databinding.SingleRowOfNewsFeedBinding
import com.app.azovatask.model.NewsDataModel
import com.app.azovatask.utils.Utils.formatTo
import com.app.azovatask.utils.Utils.toDate
import com.bumptech.glide.Glide
import timber.log.Timber

class NewsFeedAdapter(
    private val context: Context,
    private val newsList: LiveData<ArrayList<NewsDataModel>>
) : RecyclerView.Adapter<NewsFeedAdapter.MyViewHolder>() {

    inner class MyViewHolder(private var binding: SingleRowOfNewsFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemView: NewsDataModel?) {
            if (itemView?.urlToImage != null && itemView.urlToImage?.isNotEmpty() == true) {
                binding.cvImage.visibility = View.VISIBLE
                Glide.with(context).load(itemView.urlToImage).into(binding.imageView)
            } else {
                binding.cvImage.visibility = View.GONE
            }

            binding.tvTitle.text = itemView?.header
            binding.tvDescription.text = itemView?.description
            binding.tvAuthor.text = itemView?.author
            binding.tvDate.text = itemView?.pub_date?.formatTo("MM-dd-yyyy HH:mm")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = SingleRowOfNewsFeedBinding.inflate(inflater, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(newsList.value?.get(position))
    }

    override fun getItemCount(): Int {
        return newsList.value?.size ?: 0
    }
}