package pe.bbg.music.catalog.repository;

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
    List<SongEntity> findByTitleContainingIgnoreCase(String title);
    List<SongEntity> findByAlbumArtistId(UUID artistId);
}