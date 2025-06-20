package com.beatrix.to_do.dto.auth;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
