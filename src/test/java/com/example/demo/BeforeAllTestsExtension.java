package com.example.demo;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration
public class BeforeAllTestsExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        BeanFactory beanFactory = SpringExtension.getApplicationContext(context);
        BeanFactoryObjectMother.setBeanFactory(beanFactory);
    }
}
