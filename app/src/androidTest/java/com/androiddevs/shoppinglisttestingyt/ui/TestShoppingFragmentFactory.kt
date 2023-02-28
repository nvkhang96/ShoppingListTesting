package com.androiddevs.shoppinglisttestingyt.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.androiddevs.shoppinglisttestingyt.repositories.FakeShoppingRepositoryAndroidTest
import javax.inject.Inject

class TestShoppingFragmentFactory @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ImagePickFragment::class.java.name -> ImagePickFragment()
            AddShoppingItemFragment::class.java.name -> AddShoppingItemFragment()
            ShoppingFragment::class.java.name -> ShoppingFragment(
                ShoppingViewModel(FakeShoppingRepositoryAndroidTest())
            )
            else -> return super.instantiate(classLoader, className)
        }
    }
}