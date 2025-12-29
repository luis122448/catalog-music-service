package pe.bbg.music.catalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import pe.bbg.music.catalog.entity.enums.VisibilityEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_songs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SongEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "track_number")
    private Integer trackNumber;
    
    @Column(name = "duration")
    private Integer duration; // in seconds
    
    @Column(name = "explicit")
    private Boolean explicit;
    
    @Column(name = "genre")
    private String genre;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type")
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "visibility")
    private VisibilityEnum visibility = VisibilityEnum.PRIVATE;
    
    @ManyToOne
    @JoinColumn(name = "album_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private AlbumEntity album;

    public Integer getDurationMs() {
        return duration != null ? duration * 1000 : null;
    }

    public UUID getAlbumId() {
        return album != null ? album.getId() : null;
    }

    public java.util.List<UUID> getArtistIds() {
        return album != null && album.getArtist() != null ? java.util.List.of(album.getArtist().getId()) : java.util.Collections.emptyList();
    }

    // Audit Fields
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}