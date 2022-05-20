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

@Controller
@Secured(Role.USER, Role.ADMIN)
class UserController {

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

    @GetMapping("/login")
    @Secured(Role.ANONYMOUS, Role.USER, Role.ADMIN)
    fun login(model: Model): String {
        return "user/login"
    }
}
