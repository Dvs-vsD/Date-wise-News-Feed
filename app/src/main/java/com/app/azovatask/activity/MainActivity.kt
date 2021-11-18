package com.app.azovatask.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.azovatask.adapter.DoctorListAdapter
import com.app.azovatask.databinding.ActivityMainBinding
import com.app.azovatask.viewModel.MainViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var adapter: DoctorListAdapter? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.getDoctorList().observe(this, {
            tvNoDataFoundVisibility()
                adapter?.notifyDataSetChanged()
            Timber.e("list notified")
        })

        viewModel.getTotalItemCount().observe(this, {
            removeBtnVisibility()
            Timber.e("count $it")
        })

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        tvNoDataFoundVisibility()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)

        adapter = DoctorListAdapter(this, viewModel.getDoctorList())
        binding.recyclerView.adapter = adapter

        binding.btnRemove.setOnClickListener {
            alertDialog()
        }

        binding.tvSelectAll.setOnClickListener {
            if (binding.tvSelectAll.isSelected) {
                binding.tvSelectAll.isSelected = false
                viewModel.deselectAll()
                binding.tvSelectAll.text = "Select All"
            } else {
                binding.tvSelectAll.isSelected = true
                viewModel.selectAll()
                binding.tvSelectAll.text = "Deselect All"
            }
            adapter?.notifyDataSetChanged()
            removeBtnVisibility()
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this, ActivityAddDoctor::class.java)
            startActivity(intent)
        }

        binding.etSearch.addTextChangedListener {
            Timber.e(it.toString())
            if (it?.trim().toString().isNotEmpty())
                viewModel.search(it.toString())
            else
                viewModel.search("")
        }
    }

    private fun tvNoDataFoundVisibility() {
        if (viewModel.getDoctorList().value?.isEmpty() == true) {
            binding.tvNoDataFound.visibility = View.VISIBLE
        } else {
            binding.tvNoDataFound.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun removeBtnVisibility() {
        val totalItemCount: LiveData<Int> = viewModel.getTotalItemCount()
        if (totalItemCount.value!! > 0) {
            binding.btnBackgroundCard.visibility = View.VISIBLE
            binding.fab.visibility = View.GONE
            binding.btnRemove.text = "Remove(${totalItemCount.value})"
        } else {
            binding.btnBackgroundCard.visibility = View.GONE
            binding.fab.visibility = View.VISIBLE
        }
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure you want to Remove?")
        builder.setCancelable(true)
        builder.setPositiveButton("Remove") { dialog, _ ->
            removeSelectedRows()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun removeSelectedRows() {
        viewModel.removeSelectedRows()
        removeBtnVisibility()
    }
}