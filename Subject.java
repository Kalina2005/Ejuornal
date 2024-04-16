package com.ejournal.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Subject implements Serializable {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String name;
    @ManyToOne
    private UserGroup group;
    @ManyToOne
    private AppUser owner;
    @OneToMany(mappedBy = "subject")
    private List<Absence> absences = new ArrayList<>();

    public Subject(String name, UserGroup group, AppUser owner) {
        this.name = name;
        this.group = group;
        this.owner = owner;
    }

    public List<Absence> getAbsences() {
        absences.sort(Comparator.comparing(Absence::getDate));
        Collections.reverse(absences);
        return absences;
    }
}