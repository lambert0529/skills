package com.example.actuator.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomMetricsService {
    
    private final MeterRegistry meterRegistry;
    
    public void recordOrderCreated(String status, String category) {
        // 增加计数器
        meterRegistry.counter("orders.created",
            "status", status,
            "category", category
        ).increment();
    }
    
    public <T> T recordExecutionTime(String operation, java.util.function.Supplier<T> supplier) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return supplier.get();
        } finally {
            sample.stop(meterRegistry.timer("operation.time",
                "operation", operation
            ));
        }
    }
}
