package com.example.demo.controllers;

import com.example.demo.BeanFactoryObjectMother;
import com.example.demo.services.CompanyService;
import com.example.demo.services.CompanyServiceImpl;

public class CompanyServiceObjectMother {

    public static CompanyService getDefault(){
        return BeanFactoryObjectMother.getBeanFactory().getBean(CompanyServiceImpl.class);
    }

}
