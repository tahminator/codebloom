package org.patinanetwork.codebloom.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "codebloom.scheduling", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfiguration {}
