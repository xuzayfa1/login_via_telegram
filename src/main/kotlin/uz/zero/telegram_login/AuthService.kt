package uz.zero.telegram_login

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class AuthService(

    @Value("\${telegram.bot-token}")
    private val botToken: String
) {

    fun isValid(data: Map<String, String>): Boolean {
        val receivedHash = data["hash"] ?: return false

        
        val checkString = data
            .filterKeys { it != "hash" }
            .toSortedMap()
            .map { "${it.key}=${it.value}" }
            .joinToString("\n")

        
        val secretKey = MessageDigest.getInstance("SHA-256")
            .digest(botToken.toByteArray())

        
        val hmac = Mac.getInstance("HmacSHA256")
        hmac.init(SecretKeySpec(secretKey, "HmacSHA256"))
        val calculatedHash = hmac.doFinal(checkString.toByteArray())
            .joinToString("") { "%02x".format(it) }

        
        val authDate = data["auth_date"]?.toLongOrNull() ?: return false
        val now = System.currentTimeMillis() / 1000
        if (now - authDate > 300) return false

        return calculatedHash == receivedHash
    }
}
