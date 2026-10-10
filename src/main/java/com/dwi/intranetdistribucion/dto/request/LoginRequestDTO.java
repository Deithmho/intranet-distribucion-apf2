package com.dwi.intranetdistribucion.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequestDTO {
    @NotBlank @Email private String email;
    @NotBlank @Size(max=72) private String password;
}
