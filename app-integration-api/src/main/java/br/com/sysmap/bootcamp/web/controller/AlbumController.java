package br.com.sysmap.bootcamp.web.controller;


import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;

import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@Tag(name = "Albums", description = "Albums API")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final UsersService usersService;

    @Operation(summary = "Get albums from Spotify service by Text parameter", description = "Retrieve albums from Spotify based on search query.",
            responses =  {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved albums",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumModel.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid search query provided",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<AlbumModel>> getAlbums(@RequestParam("search") String search) throws IOException, ParseException, SpotifyWebApiException {
        return ResponseEntity.ok(this.albumService.getAlbums(search));
    }

    @Operation(summary = "Buy an album", description = "Feature that allows the user to purchase an album",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to invalid input data",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping("/sale")
    public ResponseEntity<Album> saveAlbum(@RequestBody Album album) {
        return ResponseEntity.ok(this.albumService.saveAlbum(album));
    }


    @Operation(summary = "Get all albums from my collection", description = "Retrieve albums in user's collection",
            responses =  {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's album collection",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Album.class))),
                    @ApiResponse(responseCode = "404", description = "User's album collection not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/my-collection")
    public ResponseEntity<List<Album>> getAlbumColletion(){
        Users users = albumService.getUser();

        return ResponseEntity.ok(albumService.getCollection(users));
    }

    @Operation(summary = "Remove an album from collection", description = "Remove an album from user's collection",
            responses =  {
                    @ApiResponse(responseCode = "200", description = "Album removed successfully"),
                    @ApiResponse(responseCode = "404", description = "Album not found in user's collection",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeAlbum(@PathVariable Long id) {
        this.albumService.deleteAlbum(id);
        return ResponseEntity.ok("Album removed successfully");
    }

}
