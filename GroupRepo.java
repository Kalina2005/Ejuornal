package com.ejournal.repo;

import com.ejournal.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepo extends JpaRepository<UserGroup, Long> {
}
