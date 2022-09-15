package main.kotlin.com.camo
import java.util.regex.Pattern
val EMAIL_ADDRESS_PATTERN = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
)

object Utils {

    fun validateSignUp(username: String?, password: String?, type: String?, email: String?, firstName: String?) {
        if (username.isNullOrBlank()) throw Exception("Invalid Username")
        if (password.isNullOrBlank() || password.length < 6) throw Exception("Weak Password")
        if (type.isNullOrBlank() || (type != "buyer" && type != "seller")) throw Exception("Invalid Type")
        if (firstName.isNullOrBlank()) throw Exception("Invalid First Name")
        if (!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) throw Exception("Invalid Email")
    }
    fun validateSignUpAdmin(username: String?, password: String?, email: String?, firstName: String?) {
        if (username.isNullOrBlank()) throw Exception("Invalid Username")
        if (password.isNullOrBlank() || password.length < 6) throw Exception("Weak Password")
        if (firstName.isNullOrBlank()) throw Exception("Invalid First Name")
        if (!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) throw Exception("Invalid Email")
    }
}
