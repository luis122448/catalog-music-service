package pe.bbg.music.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.bbg.music.catalog.entity.SongEntity;

import pe.bbg.music.catalog.entity.enums.VisibilityEnum;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SongRepository extends JpaRepository<SongEntity, UUID> {
    Optional<SongEntity> findByFilePath(String filePath);
    List<SongEntity> findByAlbumId(UUID albumId);
    List<SongEntity> findByAlbumIdAndVisibility(UUID albumId, VisibilityEnum visibility);
    Page<SongEntity> findByTitleContainingIgnoreCaseAndVisibility(String title, VisibilityEnum visibility, Pageable pageable);
    Page<SongEntity> findByVisibility(VisibilityEnum visibility, Pageable pageable);
    List<SongEntity> findByAlbumArtistId(UUID artistId);
}