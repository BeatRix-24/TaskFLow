package com.beatrix.taskflow.dto.auth;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ForgotPasswordRequest {
    private String email;
}
