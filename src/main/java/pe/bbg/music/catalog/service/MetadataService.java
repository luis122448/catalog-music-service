package pe.bbg.music.catalog.service;

import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Service;
import pe.bbg.music.catalog.dto.SongMetadataResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

@Service
@Slf4j
public class MetadataService {

    public SongMetadataResponse extractMetadata(InputStream inputStream, String fileName) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("music-", "-" + fileName.replaceAll("/", "_"));
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                inputStream.transferTo(out);
            }

            AudioFile f = AudioFileIO.read(tempFile);
            Tag tag = f.getTag();

            String title = tag != null ? tag.getFirst(FieldKey.TITLE) : null;
            if (title == null || title.isBlank()) {
                title = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
            }

            return SongMetadataResponse.builder()
                    .title(title)
                    .artist(tag != null ? tag.getFirst(FieldKey.ARTIST) : "Unknown Artist")
                    .album(tag != null ? tag.getFirst(FieldKey.ALBUM) : "Unknown Album")
                    .year(tag != null ? tag.getFirst(FieldKey.YEAR) : null)
                    .trackNumber(parseInteger(tag != null ? tag.getFirst(FieldKey.TRACK) : null))
                    .duration(f.getAudioHeader().getTrackLength())
                    .genre(tag != null ? tag.getFirst(FieldKey.GENRE) : null)
                    .mimeType(Files.probeContentType(tempFile.toPath()))
                    .build();

        } catch (Exception e) {
            log.error("Error extracting metadata from {}: {}", fileName, e.getMessage());
            return SongMetadataResponse.builder()
                    .title(fileName)
                    .artist("Unknown Artist")
                    .album("Unknown Album")
                    .build();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private Integer parseInteger(String val) {
        try {
            if (val == null) return null;
            // Handle track numbers like "1/10"
            if (val.contains("/")) {
                val = val.split("/")[0];
            }
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}