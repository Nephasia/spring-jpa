package com.example.demo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@ComponentScan("com.example.demo")
@ActiveProfiles("test")
@PropertySource("classpath:application-test.yml")
public class TestConfiguration {
}
