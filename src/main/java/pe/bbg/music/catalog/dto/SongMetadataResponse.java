package pe.bbg.music.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongMetadataResponse {
    private String title;
    private String artist;
    private String album;
    private String year;
    private Integer trackNumber;
    private Integer duration; // in seconds
    private String genre;
    private String mimeType;
}