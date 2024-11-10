package com.example.demo;

import org.springframework.beans.factory.BeanFactory;

public class BeanFactoryObjectMother {
    private static BeanFactory beanFactory;

    public static BeanFactory getBeanFactory(){
        return beanFactory;
    }

    public static void setBeanFactory(BeanFactory beanFactory){
        BeanFactoryObjectMother.beanFactory = beanFactory;
    }

}
