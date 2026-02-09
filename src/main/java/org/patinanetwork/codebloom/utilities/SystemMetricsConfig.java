package org.patinanetwork.codebloom.utilities;

import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

@Configuration
public class SystemMetricsConfig {

    @Bean
    public SystemInfo systemInfo() {
        return new SystemInfo();
    }

    @Bean
    public MeterBinder systemMemoryMetrics(SystemInfo systemInfo) {
        return registry -> {
            GlobalMemory memory = systemInfo.getHardware().getMemory();
            registry.gauge("systemInfo.memory.total", memory, GlobalMemory::getTotal);
            registry.gauge("systemInfo.memory.available", memory, GlobalMemory::getAvailable);
            registry.gauge("systemInfo.memory.used", memory, m -> m.getTotal() - m.getAvailable());
            registry.gauge(
                    "systemInfo.memory.usage",
                    memory,
                    m -> (double) (m.getTotal() - m.getAvailable()) / m.getTotal() * 100);
        };
    }
}
