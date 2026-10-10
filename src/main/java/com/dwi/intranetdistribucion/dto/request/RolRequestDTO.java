package com.dwi.intranetdistribucion.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolRequestDTO {
    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Pattern(regexp="[A-Za-z_]{3,50}")
    private String nombre;
    @jakarta.validation.constraints.Size(max=255)
    private String descripcion;
    private Set<Long> permisoIds;
}
