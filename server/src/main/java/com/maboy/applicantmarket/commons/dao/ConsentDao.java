package com.maboy.applicantmarket.commons.dao;

import com.maboy.applicantmarket.commons.dao.dto.ConsentDto;
import com.maboy.applicantmarket.commons.model.ConsentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsentDao extends JpaRepository<ConsentDto, UUID> {

    List<ConsentDto> findByUserId(UUID userId);

    Optional<ConsentDto> findByUserIdAndType(UUID userId, ConsentType type);
}