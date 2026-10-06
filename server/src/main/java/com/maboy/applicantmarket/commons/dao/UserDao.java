package com.maboy.applicantmarket.commons.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.maboy.applicantmarket.commons.dao.dto.UserDto;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<UserDto, UUID> {

    Optional<UserDto> findByUsername(String username);

    Optional<UserDto> findByUsernameOrEmail(String username, String email);

    default Optional<UserDto> findByUsernameOrEmail(String login) {
        return findByUsernameOrEmail(login, login);
    }

    boolean existsByUsernameOrEmail(String username, String email);
}