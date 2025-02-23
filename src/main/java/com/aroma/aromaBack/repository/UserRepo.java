package com.aroma.aromaBack.repository;

import com.aroma.aromaBack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}
