package com.example.gdgandroidwebinar6.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gdgandroidwebinar6.R
import com.example.gdgandroidwebinar6.domain.Forecast
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.threeten.bp.LocalDate

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
    }

    private fun setUpRefreshButton() {
        refreshButton.setOnClickListener {
            lifecycleScope.launch {
                val isSuccessful = viewModel.fetchForecastAsync().await()
                if (!isSuccessful) {
                    Toast.makeText(context, R.string.fetch_error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setUpRecyclerView() = with(weatherRecyclerView) {
        val weatherAdapter = WeatherAdapter()
        adapter = weatherAdapter
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewCoroutineScope.launch {
            viewModel.getModels().collect {
                weatherAdapter.submitList(it.forecasts)
            }
        }
    }
}
