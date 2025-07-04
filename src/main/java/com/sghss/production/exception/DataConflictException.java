// src/main/java/com/sghss/production/exception/DataConflictException.java
package com.sghss.production.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta anotação informa ao Spring que, se esta exceção for lançada,
// o status HTTP padrão deve ser 409 Conflict.
// Embora usemos um ControllerAdvice, é uma boa prática para clareza.
@ResponseStatus(HttpStatus.CONFLICT)
public class DataConflictException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataConflictException(String message) {
        super(message);
    }

    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}