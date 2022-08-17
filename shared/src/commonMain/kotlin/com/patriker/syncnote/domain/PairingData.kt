package com.patriker.syncnote.domain

import kotlinx.serialization.Serializable

@Serializable
data class PairingData(val deviceName:String, val pairingMessage:String)
