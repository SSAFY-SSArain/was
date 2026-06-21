package org.ssafy.ssarain.common.ratelimit.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.ssafy.ssarain.common.ratelimit.properties.RateLimitProperties;
import org.ssafy.ssarain.common.ratelimit.properties.RateLimitProperties.RateLimitScope;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.RETRY_AFTER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractRateLimitFilter extends OncePerRequestFilter {

    private static final String REMAINING_HEADER = "X-Rate-Limit-Remaining";

    private final RateLimitProperties  rateLimitProperties;
    private final ProxyManager<String> rateLimitProxyManager;
    private final ObjectMapper         objectMapper;

    @Override
    protected final void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String endpointKey = getEndpointKey(request);
        RateLimitProperties.EndpointConfig endpointConfig = findEndpointConfig(endpointKey);

        if(endpointConfig == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String subject = resolveSubject(request);
        if(subject == null || subject.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = buildBucketKey(subject, endpointKey);
        BucketProxy bucketProxy = rateLimitProxyManager.builder().build(key, toSupplier(endpointConfig));
        ConsumptionProbe probe = bucketProxy.tryConsumeAndReturnRemaining(1);

        if(probe.isConsumed()) {
            response.setHeader(REMAINING_HEADER, String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
            return;
        }

        sendRateLimitResponse(response, probe);
    }

    protected abstract RateLimitScope getScope();

    protected abstract String getKeyPrefix();

    protected abstract String resolveSubject(HttpServletRequest request);

    private String getEndpointKey(HttpServletRequest request) {
        return rateLimitProperties.getEndpointKey(request.getMethod(), request.getServletPath());
    }

    private RateLimitProperties.EndpointConfig findEndpointConfig(String endpointKey) {
        return rateLimitProperties.findEndpointConfig(getScope(), endpointKey);
    }

    private String buildBucketKey(String subject, String endpointKey) {
        return getKeyPrefix()
                + subject
                + ":"
                + endpointKey;
    }

    private Supplier<BucketConfiguration> toSupplier(RateLimitProperties.BucketConfig bucketConfig) {

        return () -> BucketConfiguration.builder()
                .addLimit(limit -> configureRefillStrategy(limit, bucketConfig))
                .build();
    }

    private BandwidthBuilder.BandwidthBuilderBuildStage configureRefillStrategy(
            BandwidthBuilder.BandwidthBuilderCapacityStage limit,
            RateLimitProperties.BucketConfig bucketConfig
    ) {
        Duration duration = Duration.ofMinutes(bucketConfig.getDurationMinutes());
        int capacity = bucketConfig.getCapacity();

        return switch (bucketConfig.getRefillStrategy()) {
            case INTERVAL -> limit.capacity(capacity)
                    .refillIntervally(capacity, duration);
            case GREEDY -> limit.capacity(capacity)
                    .refillGreedy(capacity, duration);
        };
    }

    private void sendRateLimitResponse(HttpServletResponse response, ConsumptionProbe probe) throws IOException {

        BaseResponse<Void> baseResponse = BaseResponse.of(ErrorCode.RATE_LIMIT_EXCEEDED);
        long retryAfterSeconds = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));

        response.setStatus(baseResponse.status());
        response.setHeader(RETRY_AFTER, String.valueOf(retryAfterSeconds));
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        objectMapper.writeValue(response.getWriter(), baseResponse);
    }
}
