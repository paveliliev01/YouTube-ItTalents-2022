package com.youtube_project.model.repositories;

import com.youtube_project.model.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findAllByEmail(String email);

    List<User> findAllByPhoneNumber(String phoneNumber);

    @Query("select u from User u where u.firstName  LIKE %:firstName% and u.lastName LIKE %:lastName%")
    List<User> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);

    Optional<User> findUserByEmail(String email);
}
