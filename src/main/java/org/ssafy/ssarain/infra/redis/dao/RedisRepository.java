package org.ssafy.ssarain.infra.redis.dao;


import java.time.Duration;
import java.util.Optional;

public interface RedisRepository {

    boolean hasKey(String key);

    boolean deleteData(String key);

    void setValue(String key, Object value);

    void setValue(String key, Object value, Duration duration);

    <T> Optional<T> getValue(String key, Class<T> type);

}
