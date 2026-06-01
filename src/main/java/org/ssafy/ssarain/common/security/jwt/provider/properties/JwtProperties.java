package org.ssafy.ssarain.common.security.jwt.provider.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    @NotBlank
    private String issuer;
    @NotBlank
    private String accessTokenSecret;
    @NotBlank
    private String refreshTokenSecret;
    @NotNull
    @Min(1)
    private Long accessTokenExpirationSeconds;
    @NotNull
    @Min(1)
    private Long refreshTokenExpirationSeconds;
}
