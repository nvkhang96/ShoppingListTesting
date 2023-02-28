package com.androiddevs.shoppinglisttestingyt.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.androiddevs.shoppinglisttestingyt.R
import com.androiddevs.shoppinglisttestingyt.other.Status
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_shopping_item.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddShoppingItemFragment() : Fragment(R.layout.fragment_add_shopping_item) {

    @Inject
    lateinit var glide: RequestManager

    lateinit var viewModel: ShoppingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ShoppingViewModel::class.java]
        subscribeToObserver()

        btnAddShoppingItem.setOnClickListener {
            viewModel.insertShoppingItem(
                etShoppingItemName.text.toString(),
                etShoppingItemAmount.text.toString(),
                etShoppingItemPrice.text.toString()
            )
        }

        ivShoppingImage.setOnClickListener {
            findNavController().navigate(
                AddShoppingItemFragmentDirections.actionAddShoppingItemFragmentToImagePickFragment()
            )
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.setCurImageUrl("")
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun subscribeToObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentImageUrl.collect {
                    glide.load(it).into(ivShoppingImage)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.insertShoppingItemStatus.collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            Snackbar.make(
                                requireActivity().rootLayout,
                                "Added Shopping Item",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }
                        Status.ERROR -> {
                            Snackbar.make(
                                requireActivity().rootLayout,
                                it.message ?: "An unknown error occurred",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        Status.LOADING -> {
                            // NO-OP
                        }
                    }
                }
            }
        }
    }
}