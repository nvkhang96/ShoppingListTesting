package com.androiddevs.shoppinglisttestingyt.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.androiddevs.shoppinglisttestingyt.R
import com.androiddevs.shoppinglisttestingyt.adapters.ImageAdapter
import com.androiddevs.shoppinglisttestingyt.other.Constants.GRID_SPAN_COUNT
import com.androiddevs.shoppinglisttestingyt.other.Constants.SEARCH_TIME_DELAY
import com.androiddevs.shoppinglisttestingyt.other.Status
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image_pick.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImagePickFragment() : Fragment(R.layout.fragment_image_pick) {

    @Inject
    lateinit var imageAdapter: ImageAdapter

    lateinit var viewModel: ShoppingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ShoppingViewModel::class.java]
        setupRecyclerView()
        subscribeToObservers()

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                if (editable.toString().isNotEmpty()) {
                    viewModel.searchForImage(editable.toString())
                }
            }
        }

        imageAdapter.setOnItemClickListener {
            findNavController().popBackStack()
            viewModel.setCurImageUrl(it)
        }
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            val urls = it.data?.hits?.map { imageResult -> imageResult.previewURL }
                            imageAdapter.images = urls ?: listOf()
                            progressBar.visibility = View.GONE
                        }
                        Status.ERROR -> {
                            Snackbar.make(
                                requireActivity().rootLayout,
                                it.message ?: "An unknown error occured.",
                                Snackbar.LENGTH_LONG
                            ).show()
                            progressBar.visibility = View.GONE
                        }
                        Status.LOADING -> {
                            progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        rvImages.apply {
            adapter = imageAdapter
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
        }
    }
}