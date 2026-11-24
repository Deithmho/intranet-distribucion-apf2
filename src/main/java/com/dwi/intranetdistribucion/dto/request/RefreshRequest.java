package com.dwi.intranetdistribucion.dto.request;
import jakarta.validation.constraints.*;
public record RefreshRequest(@NotBlank @Size(max=256) String refreshToken) {}
