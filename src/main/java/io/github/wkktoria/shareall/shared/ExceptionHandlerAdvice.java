package io.github.wkktoria.shareall.shared;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.wkktoria.shareall.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
class ExceptionHandlerAdvice {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ApiError handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
		ApiError apiError = new ApiError(400, "Validation error", request.getServletPath());

		BindingResult result = exception.getBindingResult();
		Map<String, String> validationErrors = new HashMap<>();

		for (FieldError fieldError : result.getFieldErrors()) {
			validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		apiError.setValidationErrors(validationErrors);

		return apiError;
	}
}
