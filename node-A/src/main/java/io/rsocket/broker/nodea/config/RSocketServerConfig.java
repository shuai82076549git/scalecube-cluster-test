package io.rsocket.broker.nodea.config;

import io.rsocket.core.Resume;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.logging.Logger;

@Configuration
public class RSocketServerConfig {
    private static final Logger log = Logger.getLogger(RSocketServerConfig.class.getName());


    @Bean
    public Resume resume() {
        Resume resume = new Resume()
                .sessionDuration(Duration.ofSeconds(30)) // 设置会话持续时间为15分钟
                .cleanupStoreOnKeepAlive()          // 启用 KEEPALIVE 帧清理存储
                //.storeFactory()
                .retry(Retry.fixedDelay(30, Duration.ofSeconds(1)) // 客户端重试策略
                        .doBeforeRetry(signal -> {
                            log.info("Client disconnected. Attempting to resume...");
                        }));
        return resume;
    }

    @Bean
    public RSocketServerCustomizer rSocketResume(Resume resume) {
        return rSocketServer -> rSocketServer.resume(resume);
    }
}
