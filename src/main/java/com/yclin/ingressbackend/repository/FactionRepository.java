package com.yclin.ingressbackend.repository;

import com.yclin.ingressbackend.entity.domain.Faction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactionRepository extends JpaRepository<Faction, Long> {

    /**
     * 根据阵营名称查找阵营。
     * 这在未来可能会很有用，例如通过名称来识别阵营。
     * @param name The name of the faction.
     * @return An Optional containing the found faction or empty if not found.
     */
    Optional<Faction> findByName(String name);
}