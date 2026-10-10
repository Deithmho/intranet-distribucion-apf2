package com.dwi.intranetdistribucion.exception;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorDetails> error(HttpStatus status,String mensaje) {
        return ResponseEntity.status(status).body(new ErrorDetails(LocalDateTime.now(),mensaje,status.name()));
    }
    @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<ErrorDetails> missing(ResourceNotFoundException ex) { return error(HttpStatus.NOT_FOUND,ex.getMessage()); }
    @ExceptionHandler(LockedException.class) ResponseEntity<ErrorDetails> locked(LockedException ex) { return error(HttpStatus.LOCKED,"Cuenta bloqueada"); }
    @ExceptionHandler(AuthenticationException.class) ResponseEntity<ErrorDetails> auth(AuthenticationException ex) { return error(HttpStatus.UNAUTHORIZED,"Credenciales incorrectas o usuario no habilitado"); }
    @ExceptionHandler(AccessDeniedException.class) ResponseEntity<ErrorDetails> forbidden(AccessDeniedException ex) { return error(HttpStatus.FORBIDDEN,"Permiso insuficiente"); }
    @ExceptionHandler({IllegalStateException.class,DataIntegrityViolationException.class}) ResponseEntity<ErrorDetails> conflict(Exception ex) { return error(HttpStatus.CONFLICT,"La operación contradice una regla de integridad o de negocio"); }
    @ExceptionHandler({IllegalArgumentException.class,MethodArgumentNotValidException.class,HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,ConstraintViolationException.class})
    ResponseEntity<ErrorDetails> invalid(Exception ex) { return error(HttpStatus.BAD_REQUEST,"Datos de entrada no válidos"); }
    @ExceptionHandler(Exception.class) ResponseEntity<ErrorDetails> other(Exception ex) { return error(HttpStatus.INTERNAL_SERVER_ERROR,"Error interno del servidor"); }
}
