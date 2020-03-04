package com.josephbleau.StravaMattermostConnector.repository;

import com.josephbleau.StravaMattermostConnector.model.PersistedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Persist and load tokens
 */
@Repository
public interface PersistedTokenRepository extends CrudRepository<PersistedToken, Integer> {}