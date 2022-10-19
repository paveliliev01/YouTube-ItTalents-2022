package com.youtube_project.models.user;

import antlr.collections.impl.ASTArray;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findAllByEmail(String email);

    List<User> findAllByPhoneNumber(String phoneNumber);

    List<User> findAllByFirstNameAndLastName(String firstName,String lastName);
}
