package com.example.demo.repositories;

import com.example.demo.BeanFactoryObjectMother;

public class ProjectRepositoryObjectMother {

    public static ProjectRepository getDefault(){
        return BeanFactoryObjectMother.getBeanFactory().getBean(ProjectRepository.class);
    }

}
