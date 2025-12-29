package pe.bbg.music.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.bbg.music.catalog.entity.ArtistEntity;

import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {
    Optional<ArtistEntity> findByName(String name);
    Page<ArtistEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}