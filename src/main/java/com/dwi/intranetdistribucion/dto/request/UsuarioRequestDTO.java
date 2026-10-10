package com.dwi.intranetdistribucion.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioRequestDTO {
    @NotBlank @Size(max=100) private String nombre;
    @NotBlank @Size(max=100) private String apellido;
    @NotBlank @Email @Size(max=150) private String email;
    @NotBlank @Size(min=12, max=72) private String password;
    @NotNull @Positive private Long rolId;
    @Builder.Default private Boolean esActivo = true;
}
