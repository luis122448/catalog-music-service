package pe.bbg.music.catalog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import pe.bbg.music.catalog.dto.ApiResponse;
import pe.bbg.music.catalog.dto.SongMetadataResponseLite;
import pe.bbg.music.catalog.repository.SongRepository;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@Tag (name = "Internal Music", description = "Endpoints for internal music catalog management")
public class InternalMusicController {
    
    private final SongRepository songRepository;

    @GetMapping("/songs/{id}")
    public ResponseEntity<ApiResponse<SongMetadataResponseLite>> getSong(@PathVariable UUID id) {
        return songRepository.findById(id)
                .map(song -> {
                    SongMetadataResponseLite response = SongMetadataResponseLite.builder()
                            .title(song.getTitle())
                            .filePath(song.getFilePath())
                            .fileSize(song.getFileSize())
                            .duration(song.getDuration())
                            .build();
                    return ResponseEntity.ok(ApiResponse.success(response, "Song retrieved successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}