import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptUtil {
    fun generateKey(password: String): SecretKeySpec {
        var digest = MessageDigest.getInstance("SHA-256")
        val bytes = password.toByteArray(Charset.forName("UTF-8"))
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
        val key = generateKey(password)
        val c = Cipher.getInstance("AES")

        c.init(Cipher.DECRYPT_MODE, key)

        val decodedValue: ByteArray = Base64.getDecoder().decode(outputString)

        val decValue = c.doFinal(decodedValue)
        return String(decValue)

    }
}