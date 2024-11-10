package com.example.demo.repositories;

import com.example.demo.BeanFactoryObjectMother;

public class ManagerRepositoryObjectMother {

    public static ManagerRepository getDefault(){
        return BeanFactoryObjectMother.getBeanFactory().getBean(ManagerRepository.class);
    }

}
