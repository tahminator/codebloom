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
            registry.gauge("system.info.memory.total", memory, GlobalMemory::getTotal);
            registry.gauge("system.info.memory.available", memory, GlobalMemory::getAvailable);
            registry.gauge("system.info.memory.used", memory, m -> m.getTotal() - m.getAvailable());
            registry.gauge(
                    "system.info.memory.usage",
                    memory,
                    m -> (double) (m.getTotal() - m.getAvailable()) / m.getTotal() * 100);
        };
    }
}
