package com.example.demo.repositories;

import com.example.demo.BeanFactoryObjectMother;

public class CompanyRepositoryObjectMother {

    public static CompanyRepository getDefault(){
        return BeanFactoryObjectMother.getBeanFactory().getBean(CompanyRepository.class);
    }

}
