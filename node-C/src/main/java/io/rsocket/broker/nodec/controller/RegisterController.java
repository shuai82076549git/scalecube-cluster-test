package io.rsocket.broker.nodec.controller;

import io.scalecube.cluster.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class RegisterController {
    @Autowired
    private Cluster cluster;

    // 存储活跃的客户端连接
    private final Map<String, RSocketRequester> clients = new ConcurrentHashMap<>();


    @ConnectMapping("A")
    public Mono<Void> serverConnect(RSocketRequester requester,@AuthenticationPrincipal JwtClaimAccessor jwt) {
        String username = jwt.getSubject();

        clients.put(username, requester);
        System.out.println("新客户端连接: " + username + ", 当前连接数: " + clients.size());

        // 客户端断开时清理
        requester.rsocketClient().onClose()
                .doFinally(signalType -> {
                    System.out.println("用户 " + username + " 断开连接");
                    clients.remove(username);
                })
                .subscribe();

        //return Mono.just("已注册Address为:"+ cluster.address() +" Rsocket broker节点");
        return Mono.empty();
    }


    @ConnectMapping("B")
    public Mono<Void> clientConnect(RSocketRequester requester,@AuthenticationPrincipal JwtClaimAccessor jwt) {
        String username = jwt.getSubject();

        clients.put(username, requester);
        System.out.println("新客户端连接: " + username + ", 当前连接数: " + clients.size());

        // 客户端断开时清理
        requester.rsocketClient().onClose()
                .doFinally(signalType -> {
                    System.out.println("用户 " + username + " 断开连接");
                    clients.remove(username);
                })
                .subscribe();

        //return Mono.just("已注册Address为:"+ cluster.address() +" Rsocket broker节点");
        return Mono.empty();
    }

    @MessageMapping("hello")
    public Mono<String> hello(RSocketRequester requester) {
        return Mono.just("hello");
    }
}
