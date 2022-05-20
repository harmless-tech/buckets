package tech.harmless.buckets.controller

import org.springframework.http.MediaType
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import tech.harmless.buckets.util.Role
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Controller
@Secured(Role.USER)
class UserController {

    @GetMapping("/login")
    @Secured(Role.ANONYMOUS, Role.USER)
    fun login(model: Model): String {
        return "user/login"
    }

    @GetMapping("/logout")
    @Secured(Role.ANONYMOUS, Role.USER)
    fun logout(response: HttpServletResponse) {
        val cookie = Cookie("TOKENAUTHID", null)
        cookie.maxAge = 0
        cookie.secure = false // TODO: Should this be true?
        cookie.path = "/"

        response.addCookie(cookie)
        response.sendRedirect("/logout/oauth2")
    }

    @GetMapping("/user")
    fun getUser(@AuthenticationPrincipal principal: OAuth2User, model: ModelMap): String {
        model.mergeAttributes(principal.attributes)
        return "user/user_info"
    }

    @RequestMapping(value = ["/user/json"], method = [RequestMethod.GET, RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getUserJson(@AuthenticationPrincipal principal: OAuth2User): Map<String, Any> {
        return principal.attributes
    }
}
