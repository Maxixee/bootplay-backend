package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exception.DuplicatedIdException;
import br.com.sysmap.bootcamp.domain.respository.AlbumRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class AlbumServiceTest {

    @MockBean
    private AlbumService albumService;

    @MockBean
    private AlbumRepository albumRepository;


    @Test
    @DisplayName("Should return a list of registered album collection")
    public void shouldReturnListOfRegisteredAlbums() {
        Users user1 = Users.builder()
                .id(1L)
                .email("test@test.com")
                .password("teste").build();
        Album album = Album.builder()
                .idSpotify("test")
                .name("test")
                .value(BigDecimal.TEN).build();
        List<Album> albumList = new ArrayList<>();
        albumList.add(album);

        when(albumService.getCollection(user1)).thenReturn(albumList);

    }

    @Test
    @DisplayName("Should save album")
    public void shouldSaveAlbum() {
        Album album = Album.builder()
                .idSpotify("1")
                .name("test")
                .value(BigDecimal.TEN).build();


        when(albumService.saveAlbum(any(Album.class))).thenReturn(album);
        assertNotNull(album);
    }

    @Test
    @DisplayName("Should return exception when album already saved")
    public void shouldReturnExceptionWhenAlbumAlreadySaved() {
        Album album = Album.builder()
                .idSpotify("1")
                .name("test")
                .value(BigDecimal.TEN).build();

        when(albumService.saveAlbum(any(Album.class))).thenThrow(new DuplicatedIdException("Album already saved"));

        assertThrows(DuplicatedIdException.class, () -> albumService.saveAlbum(album));
    }

    @Test
    @DisplayName("Should delete album")
    public void shouldDeleteAlbum() {
        doNothing().when(albumService).deleteAlbum(anyLong());

        albumService.deleteAlbum(1L);

        verify(albumService, times(1)).deleteAlbum(1L);
    }
}