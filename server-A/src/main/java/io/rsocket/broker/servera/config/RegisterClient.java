package io.rsocket.broker.servera.config;

import io.rsocket.core.Resume;
import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.loadbalance.RoundRobinLoadbalanceStrategy;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Configuration
public class RegisterClient {
    private static final Logger log = Logger.getLogger(RegisterClient.class.getName());
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

    //负载均衡RSocketRequester配置
    @Bean
    public RSocketRequester rSocketRequester(Resume resume, RSocketStrategies rsocketStrategies) {
        return RSocketRequester.builder()
                .rsocketConnector(rSocketConnector -> rSocketConnector.resume(resume))
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .rsocketStrategies(rsocketStrategies)
                .transports(rSocketServerInstance(), new RoundRobinLoadbalanceStrategy());
    }


    private List<String> getClusterAddresses() {
        WebClient client = WebClient.create("http://localhost:8081");
        List<String> addresses = client.get().uri("/getClusterAdresses").retrieve().bodyToMono(List.class).block();
        System.out.println("getClusterAdresses: " + addresses);
        return addresses;
    }

    public Flux<List<LoadbalanceTarget>> rSocketServerInstance() {

        List<LoadbalanceTarget> targets = getClusterAddresses().stream().map(address -> {
            String ip = address.split(":")[0];
            String port = address.split(":")[1];
            return LoadbalanceTarget.from(address, TcpClientTransport.create(ip, Integer.parseInt(port)));
        }).collect(Collectors.toList());
        return Flux.just(targets); // 静态列表，实际场景可能动态更新
    }
}
