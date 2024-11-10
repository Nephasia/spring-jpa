package com.example.demo.controllers;

import com.example.demo.BeanFactoryObjectMother;

public class CompanyControllerObjectMother {

    public static CompanyController getDefault() {
        return BeanFactoryObjectMother.getBeanFactory().getBean(CompanyController.class);
    }

}
