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
import pe.bbg.music.catalog.entity.embeddable.Image;
import pe.bbg.music.catalog.entity.enums.ReleaseTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tbl_albums")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AlbumEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "year")
    private String year;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "cover_art_path")
    private String coverArtPath; // Internal path
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tbl_album_covers", joinColumns = @JoinColumn(name = "album_id"))
    private List<Image> coverArt;

    @Column(name = "total_tracks")
    private Integer totalTracks;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ReleaseTypeEnum type;
    
    @ManyToOne
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;
    
    @OneToMany(mappedBy = "album", fetch = FetchType.EAGER)
    private List<SongEntity> songs;

    public UUID getArtistId() {
        return artist != null ? artist.getId() : null;
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