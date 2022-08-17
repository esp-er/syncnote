package com.patriker.syncnote.domain

@kotlinx.serialization.Serializable
data class PairingResponse(val ok: Boolean, val encryptedKey: String)
