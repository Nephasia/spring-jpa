package com.example.demo.repositories;

import com.example.demo.BeanFactoryObjectMother;
import com.example.demo.services.CompanyService;

public class CompanyServiceObjectMother {

    public static CompanyService getDefault(){
        return BeanFactoryObjectMother.getBeanFactory().getBean(CompanyService.class);
    }

}
