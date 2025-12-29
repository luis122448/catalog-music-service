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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tbl_artists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ArtistEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @ElementCollection
    @CollectionTable(name = "tbl_artist_images", joinColumns = @JoinColumn(name = "artist_id"))
    private List<Image> images;

    @ElementCollection
    @CollectionTable(name = "tbl_artist_genres", joinColumns = @JoinColumn(name = "artist_id"))
    @Column(name = "genre")
    private List<String> genres;

    @Column(name = "popularity")
    private Integer popularity;
    
    @OneToMany(mappedBy = "artist")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<AlbumEntity> albums;

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