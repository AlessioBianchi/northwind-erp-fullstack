package com.github.alessiobianchi.erp.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDTO(@NotBlank String currentPassword, @NotBlank String newPassword) {
}
