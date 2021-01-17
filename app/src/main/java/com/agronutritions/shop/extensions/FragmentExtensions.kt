package com.agronutritions.shop.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

/**
 * Fragment extension methods.
 */

/**
 * navigte - Extension method to navigate to a fragment using Nav Controller
 */
fun Fragment.navigate(getaction: () -> NavDirections){
    findNavController().navigate(getaction())
}

/**
 * popBackStack - Extension method to pop navigation stack
 */
fun Fragment.popBackStack(){
    findNavController().popBackStack()
}