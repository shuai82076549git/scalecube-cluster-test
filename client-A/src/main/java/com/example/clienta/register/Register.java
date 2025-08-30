package com.example.clienta.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

@Component
public class Register implements ApplicationRunner {

    @Autowired
    private RSocketRequester  rSocketRequester;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        rSocketRequester
                .route("hello")
                .retrieveMono(String.class)
                .doOnNext(v-> System.out.println(v))
                .repeat(2)
                .subscribe();
    }
}
