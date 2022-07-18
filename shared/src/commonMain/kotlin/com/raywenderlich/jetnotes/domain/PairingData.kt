package com.raywenderlich.jetnotes.domain

import kotlinx.serialization.Serializable

@Serializable
data class PairingData(val deviceName:String, val pairingCode:String)
