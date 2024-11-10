package com.example.demo.repositories;

import com.example.demo.BeanFactoryObjectMother;

public class TeamRepositoryObjectMother {

    public static TeamRepository getDefault(){
        return BeanFactoryObjectMother.getBeanFactory().getBean(TeamRepository.class);
    }

}
