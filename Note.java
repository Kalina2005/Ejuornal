package com.ejournal.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Note implements Serializable {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String file;
    private String dateWith;
    private String dateBy;

    @ManyToOne
    private AppUser owner;

    public Note(String file, String dateWith, String dateBy, AppUser owner) {
        this.file = file;
        this.dateWith = dateWith;
        this.dateBy = dateBy;
        this.owner = owner;
    }
}