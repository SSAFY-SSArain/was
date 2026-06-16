package org.ssafy.ssarain.common.util;

import java.util.List;
import java.util.function.Consumer;

public class BatchProcessor {
    
    public static final int DEFAULT_BATCH_SIZE = 250;
    
    public static <T> void process(List<T> data, Consumer<List<T>> consumer) {
        int pageCount = 1 + (data.size() - 1) / DEFAULT_BATCH_SIZE;
        for (int page = 0; page < pageCount; page++) {
            List<T> batch = data.subList(
                    page * DEFAULT_BATCH_SIZE,
                    Math.min((page + 1) * DEFAULT_BATCH_SIZE, data.size()));
            
            consumer.accept(batch);
        }
    }
}
