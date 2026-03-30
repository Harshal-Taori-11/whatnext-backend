package com.whatnext.repository;

import com.whatnext.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByUserId(Long userId);

    Optional<Tag> findByUserIdAndName(Long userId, String name);

    Boolean existsByUserIdAndName(Long userId, String name);
}
