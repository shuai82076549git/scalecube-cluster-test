package io.rsocket.broker.nodec.controller;

import io.scalecube.cluster.Cluster;
import io.scalecube.cluster.transport.api.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GossIpContorller {
    @Autowired
    private Cluster cluster;

    @GetMapping("/gossIp")
    public void grossIp() {
        cluster.spreadGossip(Message.fromData("Gossip from Alice"))
                .doOnError(System.err::println)
                .subscribe(null, Throwable::printStackTrace);
    }

    @GetMapping("/getClusterAdresses")
    public List<String> getNodeIp() {
       List<String> ipSet = cluster.members().stream().map(v -> v.address()).collect(Collectors.toList());
        return ipSet;
    }
}
