package com.vision.project.config;

import com.vision.project.services.base.ChatService;
import com.vision.project.services.base.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@ConfigurationProperties(prefix = "app.schedule")
public class ScheduleConfig implements SchedulingConfigurer {

    private String threadPrefix;

    private ChatService chatService;
    private OrderService orderService;

    public ScheduleConfig(ChatService chatService, OrderService orderService) {
        this.chatService = chatService;
        this.orderService = orderService;
    }

    @Bean
    public ThreadPoolTaskScheduler createThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix(threadPrefix);
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        chatService.setServerStartDate();
        orderService.setDates();
    }

    public String getThreadPrefix() {
        return threadPrefix;
    }

    public void setThreadPrefix(String threadPrefix) {
        this.threadPrefix = threadPrefix;
    }
}