package pe.bbg.music.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Public Music", description = "Endpoints for browsing the music catalog")
public class PublicMusicController {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final MinioService minioService;

    @GetMapping("/artists")
    @Operation(summary = "Get all artists")
    public ResponseEntity<ApiResponse<List<ArtistEntity>>> getArtists() {
        return ResponseEntity.ok(ApiResponse.success(artistRepository.findAll(), "Artists retrieved successfully"));
    }

    @GetMapping("/artists/{id}")
    @Operation(summary = "Get artist by ID")
    public ResponseEntity<ApiResponse<ArtistEntity>> getArtist(@PathVariable UUID id) {
        return artistRepository.findById(id)
                .map(artist -> ResponseEntity.ok(ApiResponse.success(artist, "Artist retrieved successfully")))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/artists/{id}/top-tracks")
    @Operation(summary = "Get top tracks for an artist")
    public ResponseEntity<ApiResponse<List<SongEntity>>> getArtistTopTracks(@PathVariable UUID id) {
        List<SongEntity> songs = songRepository.findByAlbumArtistId(id).stream().limit(10).toList();
        return ResponseEntity.ok(ApiResponse.success(songs, "Top tracks retrieved successfully"));
    }

    @GetMapping("/artists/{id}/albums")
    @Operation(summary = "Get all albums for an artist")
    public ResponseEntity<ApiResponse<List<AlbumEntity>>> getArtistAlbums(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(albumRepository.findByArtistId(id), "Albums retrieved successfully"));
    }

    @GetMapping("/albums/{id}")
    @Operation(summary = "Get album by ID")
    public ResponseEntity<ApiResponse<AlbumEntity>> getAlbum(@PathVariable UUID id) {
        return albumRepository.findById(id)
                .map(album -> ResponseEntity.ok(ApiResponse.success(album, "Album retrieved successfully")))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/albums/{id}/songs")
    @Operation(summary = "Get all songs for an album")
    public ResponseEntity<ApiResponse<List<SongEntity>>> getAlbumSongs(@PathVariable UUID id) {
        List<SongEntity> songs = songRepository.findByAlbumIdAndVisibility(id, VisibilityEnum.PUBLIC);
        return ResponseEntity.ok(ApiResponse.success(songs, "Album songs retrieved successfully"));
    }

    @GetMapping("/songs/{id}")
    @Operation(summary = "Get song by ID")
    public ResponseEntity<ApiResponse<SongEntity>> getSong(@PathVariable UUID id) {
        return songRepository.findById(id)
                .filter(song -> song.getVisibility() == VisibilityEnum.PUBLIC)
                .map(song -> ResponseEntity.ok(ApiResponse.success(song, "Song retrieved successfully")))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search for artists, albums, or tracks")
    public ResponseEntity<ApiResponse<Map<String, Object>>> search(@RequestParam String q, @RequestParam(required = false) String type) {
        Map<String, Object> results = new HashMap<>();
        
        if (type == null || "artist".equalsIgnoreCase(type)) {
            results.put("artists", artistRepository.findByNameContainingIgnoreCase(q));
        }
        if (type == null || "album".equalsIgnoreCase(type)) {
            results.put("albums", albumRepository.findByTitleContainingIgnoreCase(q));
        }
        if (type == null || "track".equalsIgnoreCase(type)) {
            results.put("tracks", songRepository.findByTitleContainingIgnoreCase(q));
        }
        
        return ResponseEntity.ok(ApiResponse.success(results, "Search results retrieved successfully"));
    }
    
    @GetMapping("/browse/new-releases")
    @Operation(summary = "Get recent releases")
    public ResponseEntity<ApiResponse<List<AlbumEntity>>> getNewReleases() {
        return ResponseEntity.ok(ApiResponse.success(albumRepository.findTop10ByOrderByReleaseDateDesc(), "New releases retrieved successfully"));
    }
}