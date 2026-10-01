package com.charan.yourday.presentation.home

sealed interface HomeEffect {
    data class ShowToast(val message : String) : HomeEffect

}