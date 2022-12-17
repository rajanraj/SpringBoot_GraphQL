package com.graphql.demo.Repository;

import com.graphql.demo.Entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepo extends JpaRepository<PersonEntity,Integer> {
    PersonEntity findByEmail(String email);
}
