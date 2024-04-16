package com.ejournal.model;

import com.ejournal.model.enums.Reason;
import com.ejournal.model.enums.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AppUser implements UserDetails, Serializable {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String username;
    private String password;
    private String fio;
    private String tel = "";
    private String email = "";
    private boolean enabled = false;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @ManyToOne
    private UserGroup group;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Subject> subjects = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Absence> absences = new ArrayList<>();
    @OneToMany(mappedBy = "owner")
    private List<Note> notes = new ArrayList<>();

    public AppUser(String username, String password, String fio, Role role) {
        this.username = username;
        this.password = passwordEncoder().encode(password);
        this.fio = fio;
        this.role = role;
    }

    public String getSubjectsToString() {
        StringJoiner joiner = new StringJoiner(", ");
        for (Subject i : subjects) {
            joiner.add(i.getName());
        }
        return joiner.toString();
    }

    public int getAbsencesDisrespectful() {
        return absences.stream().reduce(0, (i, absence) -> {
            if (absence.getReason() == Reason.DISRESPECTFUL) return i + absence.getCount();
            return i;
        }, Integer::sum);
    }

    public int getAbsencesRespectful() {
        return absences.stream().reduce(0, (i, absence) -> {
            if (absence.getReason() == Reason.RESPECTFUL) return i + absence.getCount();
            return i;
        }, Integer::sum);
    }

    public String getAttendance() {
        int count = getAbsencesDisrespectful();

        if (count < 10) {
            return "Хорошая посещаемость";
        } else if (count < 30) {
            return "Посещаемость в пределах нормы";
        } else {
            return "Плохая посещаемость";
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(getRole());
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}