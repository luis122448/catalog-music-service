package pe.bbg.music.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.bbg.music.catalog.dto.ApiResponse;
import pe.bbg.music.catalog.service.CatalogService;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
@Tag(name = "Scan", description = "Endpoints for library scanning")
@SecurityRequirement(name = "bearerAuth")
public class ScanController {

    private final CatalogService catalogService;

    @PostMapping("/start")
    @Operation(summary = "Start library scan")
    public ResponseEntity<ApiResponse<String>> startScan() {
        // Run async
        CompletableFuture.runAsync(catalogService::scanLibrary);
        return ResponseEntity.accepted().body(ApiResponse.success("Scan started successfully", "The scanning process has been initiated in the background"));
    }
}
