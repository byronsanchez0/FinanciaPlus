package com.financiaplus.backend.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationRepository
        extends JpaRepository<Application, UUID> {

    boolean existsByIdentityNumberIgnoreCase(String identityNumber);

    boolean existsByIdentityNumberIgnoreCaseAndIdNot(
            String identityNumber,
            UUID id
    );
}
