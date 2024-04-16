package com.ejournal.repo;

import com.ejournal.model.AppUser;
import com.ejournal.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);

    List<AppUser> findAllByRoleAndGroupNull(Role role);

    List<AppUser> findAllByRole(Role role);

    List<AppUser> findAllByRoleAndFioContaining(Role role, String fio);
}
