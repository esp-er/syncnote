package com.patriker.syncnote.domain

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptUtil {
    fun generateKey(password: String): SecretKeySpec {
        var digest = MessageDigest.getInstance("SHA-256")
        //val bytes = password.toByteArray(Charset.forName("UTF-8"))
        val bytes = password.toByteArray(StandardCharsets.ISO_8859_1)
        digest.update(bytes, 0, bytes.size)
        val key: ByteArray = digest.digest()

        val secretKeySpec = SecretKeySpec(key, "AES")
        return secretKeySpec
    }


    fun encrypt(data: String, password: String): String {
        val key = generateKey(password)
        val c = Cipher.getInstance("AES")
        c.init(Cipher.ENCRYPT_MODE, key)

        val encVal: ByteArray = c.doFinal(data.toByteArray())
        val encryptedValue = Base64.getEncoder().encodeToString(encVal)

        return encryptedValue
    }

    fun decrypt(outputString: String, password: String): String {
        println("decrfypt")
        val key = generateKey(password)
        val c = Cipher.getInstance("AES")

        println("decrfypt2")

        c.init(Cipher.DECRYPT_MODE, key)

        println("decrfypt3")

        val decodedValue: ByteArray = Base64.getDecoder().decode(outputString)

        println("decrfypt4")
        var decValue: ByteArray?
        try {
            decValue = c.doFinal(decodedValue)
        }
        catch(e: Exception){
            e.printStackTrace()
            return "ERROR"
        }
        println("decrfypt5")
        return String(decValue!!)
    }
}