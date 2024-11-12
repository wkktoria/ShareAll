package io.github.wkktoria.shareall.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Slf4j
@RestController
class ErrorHandler implements ErrorController {
    private final ErrorAttributes errorAttributes;

    ErrorHandler(final ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    ApiError handleError(WebRequest webRequest) {
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.PATH, ErrorAttributeOptions.Include.STATUS));

        log.info(attributes.toString());

        String message = (String) attributes.get("message");
        String url = (String) attributes.get("path");
        int status = (Integer) attributes.get("status");

        return new ApiError(status, message, url);
    }
}
