package org.ssafy.ssarain.domain.brain.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.brain.model.Brain;

public interface BrainRepository extends JpaRepository<Brain, Integer> {
    
    boolean existsByName(String name);
    
    Page<Brain> findByNameContaining(String name, Pageable pageable);
    
}
