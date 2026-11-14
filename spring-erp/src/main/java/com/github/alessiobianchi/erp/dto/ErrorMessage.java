package com.github.alessiobianchi.erp.dto;

import java.time.LocalDateTime;

public record ErrorMessage(int status, String message, LocalDateTime timestamp) {

    public ErrorMessage(int status, String message) {
        this(status, message, LocalDateTime.now());
    }
}
