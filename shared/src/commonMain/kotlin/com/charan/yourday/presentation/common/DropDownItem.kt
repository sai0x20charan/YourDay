package com.charan.yourday.presentation.common

data class DropDownItem(
    val title: String,
    val icon: Any? = null,
    val onClick: (() -> Unit)? = null,
)
