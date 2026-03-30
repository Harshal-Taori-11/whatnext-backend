package com.whatnext.repository;

import com.whatnext.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserIdOrderByUpdatedAtDesc(Long userId);

    Page<Note> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Note> searchNotes(@Param("userId") Long userId, @Param("search") String search);
}
