package com.ecommerce.backend.domain.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data @Accessors(chain = true)
public class TokenRequest {
    private String refreshToken;
}