package org.ssafy.ssarain.common.config;

import org.ssafy.ssarain.common.ratelimit.properties.RateLimitProperties;
import org.ssafy.ssarain.common.security.config.properties.CorsProperties;
import org.ssafy.ssarain.common.security.jwt.provider.properties.JwtProperties;
import org.ssafy.ssarain.infra.ai.gemini.properties.GeminiProperties;
import org.ssafy.ssarain.infra.config.properties.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        CorsProperties.class,
        JwtProperties.class,
        RedisProperties.class,
        GeminiProperties.class,
        RateLimitProperties.class
})
public class EnableConfigurationPropertiesConfig {
}
