package com.dwi.intranetdistribucion.dto.request;
import jakarta.validation.constraints.*;
public final class CatalogoDTO {
    private CatalogoDTO() {}
    public record SedeRequest(@NotBlank @Size(max=30) String codigo, @NotBlank @Size(max=120) String nombre, @NotBlank @Size(max=250) String direccion) {}
    public record AlmacenRequest(@NotBlank @Size(max=30) String codigo, @NotBlank @Size(max=120) String nombre, @NotNull @Positive Long sedeId) {}
    public record CategoriaRequest(@NotBlank @Size(max=100) String nombre, @Size(max=250) String descripcion) {}
    public record ProductoRequest(@NotBlank @Size(max=30) String codigo, @NotBlank @Size(max=120) String nombre, @NotBlank @Size(max=30) String unidadMedida, @NotNull @Min(0) Integer stockMinimo, @NotNull @Positive Long categoriaId) {}
    public record EntradaRequest(@NotNull @Positive Long productoId, @NotNull @Positive Long almacenId, @NotNull @Min(1) @Max(1000000) Integer cantidad, @NotBlank @Size(max=200) String motivo) {}
    public record TrasladoRequest(@NotNull @Positive Long productoId, @NotNull @Positive Long origenId, @NotNull @Positive Long destinoId, @NotNull @Min(1) @Max(1000000) Integer cantidad, @NotBlank @Size(max=200) String motivo) {}
}
