package pe.bbg.music.catalog.service;

import io.minio.messages.Item;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.bbg.music.catalog.dto.SongMetadataResponse;
import pe.bbg.music.catalog.entity.AlbumEntity;
import pe.bbg.music.catalog.entity.ArtistEntity;
import pe.bbg.music.catalog.entity.SongEntity;
import pe.bbg.music.catalog.entity.enums.VisibilityEnum;
import pe.bbg.music.catalog.repository.AlbumRepository;
import pe.bbg.music.catalog.repository.ArtistRepository;
import pe.bbg.music.catalog.repository.SongRepository;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {

    private final MinioService minioService;
    private final MetadataService metadataService;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    public void scanLibrary() {
        log.info("Starting library scan...");
        List<Item> items = minioService.listObjects();
        log.info("Found {} items in MinIO", items.size());

        for (Item item : items) {
            if (item.isDir()) continue;
            
            String objectName = item.objectName();
            if (isAudioFile(objectName)) {
                if (songRepository.findByFilePath(objectName).isPresent()) {
                    log.debug("Song already exists: {}", objectName);
                    continue;
                }

                try {
                    log.info("Processing new song: {}", objectName);
                    processNewSong(item, items);
                } catch (Exception e) {
                    log.error("Error processing song: {}", objectName, e);
                }
            }
        }
        log.info("Library scan completed.");
    }

    private boolean isAudioFile(String name) {
        String lower = name.toLowerCase();
        return lower.endsWith(".mp3") || lower.endsWith(".flac") || lower.endsWith(".ogg") || lower.endsWith(".m4a");
    }

    @Transactional
    protected void processNewSong(Item item, List<Item> allItems) throws Exception {
        String objectName = item.objectName();
        try (InputStream stream = minioService.getObject(objectName)) {
            SongMetadataResponse metadata = metadataService.extractMetadata(stream, objectName);

            // 1. Artist
            ArtistEntity artist = artistRepository.findByName(metadata.getArtist())
                    .orElseGet(() -> artistRepository.save(
                            ArtistEntity.builder().name(metadata.getArtist()).build()));

            // 2. Album
            AlbumEntity album = albumRepository.findByTitleAndArtist(metadata.getAlbum(), artist)
                    .orElseGet(() -> {
                        java.time.LocalDate releaseDate = null;
                        if (metadata.getYear() != null && metadata.getYear().matches("\\d{4}")) {
                             releaseDate = java.time.LocalDate.of(Integer.parseInt(metadata.getYear()), 1, 1);
                        }
                        return albumRepository.save(
                            AlbumEntity.builder()
                                    .title(metadata.getAlbum())
                                    .artist(artist)
                                    .year(metadata.getYear())
                                    .releaseDate(releaseDate)
                                    .build());
                    });

            // 2.1 Check for cover art if not present
            if (album.getCoverArtPath() == null) {
                String folderPath = "";
                int lastSlash = objectName.lastIndexOf('/');
                if (lastSlash != -1) {
                    folderPath = objectName.substring(0, lastSlash + 1);
                }
                
                final String fPath = folderPath;
                String[] commonCoverNames = {"cover.jpg", "cover.png", "folder.jpg", "folder.png", "front.jpg"};
                
                for (String coverName : commonCoverNames) {
                    String potentialCover = fPath + coverName;
                    boolean exists = allItems.stream().anyMatch(i -> i.objectName().equalsIgnoreCase(potentialCover));
                    
                    if (exists) {
                        album.setCoverArtPath(potentialCover);
                        albumRepository.save(album);
                        break;
                    }
                }
            }

            // 3. Song
            SongEntity song = SongEntity.builder()
                    .title(metadata.getTitle())
                    .trackNumber(metadata.getTrackNumber())
                    .duration(metadata.getDuration())
                    .genre(metadata.getGenre())
                    .filePath(objectName)
                    .fileSize(item.size())
                    .mimeType(metadata.getMimeType())
                    .album(album)
                    .visibility(VisibilityEnum.PRIVATE)
                    .build();

            songRepository.save(song);
        }
    }
}