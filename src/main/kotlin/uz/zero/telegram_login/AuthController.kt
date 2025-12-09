package uz.zero.telegram_login

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"])
class AuthController(
    private val telegramAuthService: AuthService
) {

    @PostMapping("/telegram")
    fun telegramLogin(@RequestBody request: TelegramLoginRequest): ResponseEntity<Any> {

        val dataMap = mutableMapOf<String, String>()

        
        dataMap["id"] = request.id.toString()
        dataMap["first_name"] = request.first_name
        dataMap["auth_date"] = request.auth_date.toString()
        dataMap["hash"] = request.hash

        request.last_name?.let { dataMap["last_name"] = it }
        request.username?.let { dataMap["username"] = it }
        request.photo_url?.let { dataMap["photo_url"] = it }

        if (!telegramAuthService.isValid(dataMap)) {
            return ResponseEntity.status(403)
                .body(mapOf("success" to false, "error" to "Invalid Telegram data"))
        }

        val userResponse = mapOf(
            "id" to request.id,
            "name" to request.first_name,
            "username" to request.username,
            "photo" to request.photo_url
        )

        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "message" to "Muvaffaqiyatli kirish!",
                "user" to userResponse,
            )
        )
    }
}
