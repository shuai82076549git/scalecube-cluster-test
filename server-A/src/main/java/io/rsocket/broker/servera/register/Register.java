package io.rsocket.broker.servera.register;

import io.rsocket.metadata.WellKnownMimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Component
public class Register implements ApplicationRunner {

    @Autowired
    private RSocketRequester  rSocketRequester;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("=====================");
        // 定义元数据的 MIME 类型（可以使用自定义 MIME 类型或标准类型）
        MimeType authMimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());

        // 创建元数据，包含 token

        rSocketRequester
                .route("hello")
                .metadata("",authMimeType)
                .retrieveFlux(String.class)
                .doOnNext(v-> System.out.println(v))
                .subscribe();
    }
}
