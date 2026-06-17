package com.jeonny.backend.newsletter;

import java.util.Map;

public record NewsletterResponseDto(
    String title,
    Map<String, Object> metadata
) {
}