package com.example.neuron;

import com.example.neuron.config.ClusterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

@EnableScheduling
public class NeuronApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeuronApplication.class, args);
	}

}
