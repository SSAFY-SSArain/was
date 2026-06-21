package org.ssafy.ssarain.infra.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.StringCodec;
import org.ssafy.ssarain.infra.config.properties.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Bucket4jConfig {

    @Bean(destroyMethod = "shutdown")
    public RedisClient bucket4jRedisClient(RedisProperties redisProperties) {

        RedisURI.Builder builder = RedisURI.builder()
                .withHost(redisProperties.getHost())
                .withPort(redisProperties.getPort());

        if(redisProperties.getPassword() != null && !redisProperties.getPassword().isBlank()) {
            builder.withPassword(redisProperties.getPassword().toCharArray());
        }

        return RedisClient.create(builder.build());
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, byte[]> bucket4jRedisConnection(
            RedisClient bucket4jRedisClient
    ) {

        return bucket4jRedisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    }

    @Bean
    public ProxyManager<String> rateLimitProxyManager(
            StatefulRedisConnection<String, byte[]> bucket4jRedisConnection
    ) {

        return Bucket4jLettuce.casBasedBuilder(bucket4jRedisConnection) // cas 방식
                .expirationAfterWrite( // 만료 정책
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1))
                )
                .build();
    }
}
