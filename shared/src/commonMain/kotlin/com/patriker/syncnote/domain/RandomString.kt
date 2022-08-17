package com.patriker.syncnote.domain

import org.apache.commons.lang3.RandomStringUtils

fun random8Id(): String{
    val res = RandomStringUtils.randomAlphabetic(8)
    return res.uppercase()
}