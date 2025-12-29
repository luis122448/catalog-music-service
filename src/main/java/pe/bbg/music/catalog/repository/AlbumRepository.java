package pe.bbg.music.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.bbg.music.catalog.entity.AlbumEntity;
import pe.bbg.music.catalog.entity.ArtistEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<AlbumEntity, UUID> {
    Optional<AlbumEntity> findByTitleAndArtist(String title, ArtistEntity artist);
    List<AlbumEntity> findByArtistId(UUID artistId);
    Page<AlbumEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<AlbumEntity> findAllByOrderByReleaseDateDesc(Pageable pageable);
}