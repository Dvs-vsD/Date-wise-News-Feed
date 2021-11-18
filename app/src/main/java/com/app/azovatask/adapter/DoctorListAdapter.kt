package com.app.azovatask.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.app.azovatask.activity.ActivityAddDoctor
import com.app.azovatask.R
import com.app.azovatask.databinding.SingleRowOfDoctorListBinding
import com.app.azovatask.model.DoctorDetailsRealmModel
import com.app.azovatask.viewModel.MainViewModel
import com.bumptech.glide.Glide
import timber.log.Timber


class DoctorListAdapter(
    private val context: Context,
    private var list: LiveData<ArrayList<DoctorDetailsRealmModel>>?
) :
    RecyclerView.Adapter<DoctorListAdapter.MyViewHolder>() {

    private val viewModel =
        ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java)

    inner class MyViewHolder(private val binding: SingleRowOfDoctorListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DoctorDetailsRealmModel?) {

            Glide.with(context).load(R.drawable.doctor1).into(binding.imageView)
            binding.tvDoctorName.text = item?.docName ?: ""
            binding.tvSpecialization.text = item?.specialization ?: ""

            if (item?.checkedStatus == true) {
                Glide.with(context).load(R.drawable.ic_baseline_checkbox_checked_24)
                    .into(binding.ivCheckBox)
            } else {
                Glide.with(context).load(R.drawable.ic_baseline_checkbox_unchecked_24)
                    .into(binding.ivCheckBox)
            }

            binding.ivCheckBox.setOnClickListener {
                Timber.e(adapterPosition.toString())

                if (item?.checkedStatus == true) {
                    Glide.with(context).load(R.drawable.ic_baseline_checkbox_unchecked_24)
                        .into(binding.ivCheckBox)
                    item.checkedStatus = false
                    viewModel.addCount(-1)
                } else {
                    Glide.with(context).load(R.drawable.ic_baseline_checkbox_checked_24)
                        .into(binding.ivCheckBox)
                    item?.checkedStatus = true
                    viewModel.addCount(1)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = SingleRowOfDoctorListBinding.inflate(inflater, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list?.value?.get(position))

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ActivityAddDoctor::class.java)
            intent.putExtra("isUpdate", true)
            intent.putExtra("id", list?.value?.get(position)?.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list?.value?.size ?: 0
    }
}