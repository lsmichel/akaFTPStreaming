package com.example.demo.FTPserver.AkkaSpringExtentions;


import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import akka.actor.Props;

import static com.example.demo.FTPserver.AkkaSpringExtentions.SpringExtension.SPRING_EXTENSION_PROVIDER;


@Configuration
@Order(2)
@ComponentScan
public class AppConfiguration {

	@Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("akka-spring-demo");
        SPRING_EXTENSION_PROVIDER.get(system).initialize(applicationContext);
        return system;
    }
  
}