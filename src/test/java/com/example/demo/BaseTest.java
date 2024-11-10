package com.example.demo;

import com.example.demo.BeforeAllTestsExtension;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
@ExtendWith(BeforeAllTestsExtension.class)
public abstract class BaseTest {
}

