package tech.harmless.buckets.error

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

@Controller
class CustomErrorController {

    @GetMapping("/error")
    fun errorHandler(request: HttpServletRequest, model: Model): String {
        val statusCode: Any = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
        model.addAttribute("code", statusCode.toString())
        return "error/error"
    }
}