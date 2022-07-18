package com.raywenderlich.jetnotes.domain

import androidx.compose.ui.text.toUpperCase
import org.apache.commons.lang3.RandomStringUtils

fun random8Id(): String{
    val res = RandomStringUtils.randomAlphabetic(8)
    return res.toUpperCase()
}