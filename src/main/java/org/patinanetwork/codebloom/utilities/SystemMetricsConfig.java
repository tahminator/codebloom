package org.patinanetwork.codebloom.utilities;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.patinanetwork.codebloom.utilities.sha.CommitShaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

@Configuration
@EnableConfigurationProperties(CommitShaProperties.class)
public class SystemMetricsConfig {

    @Bean
    public SystemInfo systemInfo() {
        return new SystemInfo();
    }

    @Bean
    public MeterBinder applicationInfoMetrics(CommitShaProperties commitShaProperties) {
        return registry -> {
            var tags = Tags.of(Tag.of("sha", commitShaProperties.getSha()));
            registry.gauge("application.info", tags, 1, n -> 1.0);
        };
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
