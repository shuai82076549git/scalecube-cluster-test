package io.rsocket.broker.nodea.config;

import io.scalecube.cluster.Cluster;
import io.scalecube.cluster.ClusterImpl;
import io.scalecube.cluster.ClusterMessageHandler;
import io.scalecube.cluster.membership.MembershipEvent;
import io.scalecube.cluster.transport.api.Message;
import io.scalecube.cluster.transport.api.TransportConfig;
import io.scalecube.transport.netty.tcp.TcpTransportFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Collections;

@Configuration
public class ClusterConfig {

    @Bean
    public Cluster cluster() {
        Cluster Alice = new ClusterImpl()
               // .config(opts -> opts.memberAlias("Alice"))
               // .config(opts -> opts.metadata(Collections.singletonMap("name", "Alice")))
                .transport(c -> TransportConfig.defaultConfig().port(57765))
                .membership(opts -> opts.seedMembers("10.0.0.144:57765"))
                .transportFactory(TcpTransportFactory::new)
                .handler(
                        cluster -> {
                            return new ClusterMessageHandler() {
                                @Override
                                public void onMembershipEvent(MembershipEvent event) {
                                    System.out.println(LocalDateTime.now() + " Alice received: " + event);
                                }

                                @Override
                                public void onGossip(Message gossip) {
                                    System.out.println("Alice heard: " + gossip.data());
                                }
                            };
                        })
                .startAwait();
        return Alice;
    }
}
