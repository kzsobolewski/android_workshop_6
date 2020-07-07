package com.example.gdgandroidwebinar6.ui.main

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gdgandroidwebinar6.R
import com.example.gdgandroidwebinar6.WeatherLocation
import com.example.gdgandroidwebinar6.clicks
import com.example.gdgandroidwebinar6.consume
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private val Fragment.viewCoroutineScope: LifecycleCoroutineScope
    get() = viewLifecycleOwner.lifecycleScope

class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpRefreshButton()
        setUpSpinner()
    }

    private fun setUpRefreshButton() {
        viewCoroutineScope.launch {
            refreshButton.clicks()
                .collect { viewModel.fetchForecast(viewModel.models.value.location) }
        }
    }

    private fun setUpSpinner() {
        val locationNameList = WeatherLocation.values().map { it.name }
        ArrayAdapter<String>(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            locationNameList
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = adapter
        }
        locationSpinner.onItemSelectedListener = locationSpinnerListener
    }


    private val locationSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            adapterView: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            viewModel.fetchForecast(
                WeatherLocation.valueOf(
                    adapterView?.getItemAtPosition(
                        position
                    ).toString()
                )
            )
        }

        override fun onNothingSelected(p0: AdapterView<*>?) = Unit
    }


    private fun setUpRecyclerView() = with(weatherRecyclerView) {
        val weatherAdapter = WeatherAdapter()
        adapter = weatherAdapter
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        observeUiModels(weatherAdapter)
    }

    private fun observeUiModels(weatherAdapter: WeatherAdapter) {
        viewCoroutineScope.launch {
            viewModel.models.collect {
                weatherAdapter.submitList(it.forecasts)
                loadingContainer.isVisible = it.isLoading
                it.error.consume {
                    Toast.makeText(context, R.string.fetch_error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
