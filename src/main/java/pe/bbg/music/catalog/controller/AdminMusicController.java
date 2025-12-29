package pe.bbg.music.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.bbg.music.catalog.dto.ApiResponse;
import pe.bbg.music.catalog.entity.AlbumEntity;
import pe.bbg.music.catalog.entity.ArtistEntity;
import pe.bbg.music.catalog.entity.SongEntity;
import pe.bbg.music.catalog.entity.enums.VisibilityEnum;
import pe.bbg.music.catalog.repository.AlbumRepository;
import pe.bbg.music.catalog.repository.ArtistRepository;
import pe.bbg.music.catalog.repository.SongRepository;
import pe.bbg.music.catalog.service.MinioService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Music", description = "Administrative endpoints for catalog management")
@SecurityRequirement(name = "bearerAuth")
public class AdminMusicController {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final MinioService minioService;

    // --- READ OPERATIONS (Access to EVERYTHING) ---

    @GetMapping("/artists")
    @Operation(summary = "Get all artists (Admin)")
    public ResponseEntity<ApiResponse<List<ArtistEntity>>> getArtists() {
        return ResponseEntity.ok(ApiResponse.success(artistRepository.findAll(), "All artists retrieved successfully"));
    }

    @GetMapping("/artists/{id}/albums")
    @Operation(summary = "Get artist albums (Admin)")
    public ResponseEntity<ApiResponse<List<AlbumEntity>>> getArtistAlbums(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(albumRepository.findByArtistId(id), "Artist albums retrieved successfully"));
    }

    @GetMapping("/albums/{id}")
    @Operation(summary = "Get album by ID (Admin)")
    public ResponseEntity<ApiResponse<AlbumEntity>> getAlbum(@PathVariable UUID id) {
        return albumRepository.findById(id)
                .map(album -> ResponseEntity.ok(ApiResponse.success(album, "Album retrieved successfully")))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/albums/{id}/cover")
    @Operation(summary = "Get album cover URL (Admin)")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAlbumCover(@PathVariable UUID id) {
        return albumRepository.findById(id)
                .map(album -> {
                    if (album.getCoverArtPath() == null) return ResponseEntity.notFound().<ApiResponse<Map<String, String>>>build();
                    try {
                        String url = minioService.getPresignedUrl(album.getCoverArtPath());
                        return ResponseEntity.ok(ApiResponse.success(Map.of("coverUrl", url), "Cover URL retrieved successfully"));
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError().<ApiResponse<Map<String, String>>>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/albums/{id}/songs")
    @Operation(summary = "Get album songs (Admin)")
    public ResponseEntity<ApiResponse<List<SongEntity>>> getAlbumSongs(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(songRepository.findByAlbumId(id), "Album songs retrieved successfully"));
    }

    @GetMapping("/songs/{id}")
    @Operation(summary = "Get song by ID (Admin)")
    public ResponseEntity<ApiResponse<SongEntity>> getSong(@PathVariable UUID id) {
        return songRepository.findById(id)
                .map(song -> ResponseEntity.ok(ApiResponse.success(song, "Song retrieved successfully")))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- WRITE OPERATIONS ---

    @PatchMapping("/songs/{id}/visibility")
    @Operation(summary = "Update song visibility")
    public ResponseEntity<ApiResponse<SongEntity>> updateVisibility(@PathVariable UUID id, @RequestParam VisibilityEnum visibility) {
        return songRepository.findById(id)
                .map(song -> {
                    song.setVisibility(visibility);
                    return ResponseEntity.ok(ApiResponse.success(songRepository.save(song), "Song visibility updated successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}