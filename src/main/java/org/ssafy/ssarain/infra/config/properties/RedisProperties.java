package org.ssafy.ssarain.infra.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
@Getter @Setter
public class RedisProperties {
    private String host;
    private int port;
    private String password;
}
