package com.beatrix.taskflow.dto.auth;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
