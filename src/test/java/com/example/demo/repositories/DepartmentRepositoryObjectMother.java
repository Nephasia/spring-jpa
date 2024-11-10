package com.example.demo.repositories;

import com.example.demo.BeanFactoryObjectMother;

public class DepartmentRepositoryObjectMother {

    public static DepartmentRepository getDefault(){
        return BeanFactoryObjectMother.getBeanFactory().getBean(DepartmentRepository.class);
    }

}
