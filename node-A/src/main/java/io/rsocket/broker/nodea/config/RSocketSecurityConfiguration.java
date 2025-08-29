package io.rsocket.broker.nodea.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.handler.invocation.reactive.CurrentSecurityContextArgumentResolver;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collections;

@Configuration
@EnableRSocketSecurity
@Slf4j
public class RSocketSecurityConfiguration {


    @Bean
    public RSocketStrategies rSocketStrategies() {
        return RSocketStrategies.builder()
                .decoder(new Jackson2JsonDecoder())
                .encoder(new Jackson2JsonEncoder())
                .build();
    }

    @Bean
    PayloadSocketAcceptorInterceptor interceptor(RSocketSecurity security) {
        security
                .authorizePayload(spec ->
                        spec.setup().authenticated().  // 要求连接建立时认证
                                anyRequest().authenticated()
                                //.route("greetings").authenticated()
                                .anyExchange().permitAll())
                .jwt(Customizer.withDefaults());
        return security.build();
    }


    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec("JAC1O17W1F3QB9E8B4B1MT6QKYOQB36V".getBytes(), mac.getAlgorithm());
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())  // 🔹 关闭 CSRF 保护
                .formLogin(v->v.disable())
                .authorizeExchange(exchangeSpec ->{
                    //静态资源所有人都可以访问
                    exchangeSpec.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            .anyExchange().permitAll();
                })
                .build();
    }
}
