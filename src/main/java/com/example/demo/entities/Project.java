package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonBackReference
    private Team team;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    @JsonManagedReference
    private Manager manager;

    public void setManager(Manager manager) {
        if (manager != null) {
            this.manager = manager;
            manager.setProject(this);
        }
    }

}
