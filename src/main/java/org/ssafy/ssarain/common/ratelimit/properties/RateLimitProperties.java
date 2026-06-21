package org.ssafy.ssarain.common.ratelimit.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("app.rate-limit")
@Getter
@Validated
public class RateLimitProperties {

    private final Map<String, @Valid EndpointConfig> endpoints = new HashMap<>();

    public String getEndpointKey(String method, String requestURI) {
        return method + " " + requestURI;
    }

    public EndpointConfig findEndpointConfig(RateLimitScope scope, String endpointKey) {
        EndpointConfig config = getEndpointConfig(endpointKey);
        if(config == null || !config.matchesScope(scope)) {
            return null;
        }

        return config;
    }

    private EndpointConfig getEndpointConfig(String endpointKey) {
        return endpoints.get(endpointKey);
    }

    @Getter
    @Setter
    public static class BucketConfig {
        @Min(1)
        private int capacity = 100;
        @Min(1)
        private int durationMinutes = 1;
        private RefillStrategy refillStrategy = RefillStrategy.GREEDY;
    }

    @Getter
    @Setter
    public static class EndpointConfig extends BucketConfig {

        private RateLimitScope scope = RateLimitScope.ANONYMOUS;

        private boolean matchesScope(RateLimitScope requestScope) {

            return scope == requestScope;
        }
    }

    public enum RefillStrategy {
        GREEDY,
        INTERVAL
    }

    public enum RateLimitScope {
        ANONYMOUS,
        AUTHENTICATED
    }
}
