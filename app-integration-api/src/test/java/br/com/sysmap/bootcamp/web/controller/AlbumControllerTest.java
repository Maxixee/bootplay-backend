package br.com.sysmap.bootcamp.web.controller;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exception.EntityNotFoundException;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AlbumController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService albumService;


    @Test
    @DisplayName("Should return user's album collection")
    public void shouldReturnUserAlbumCollection() throws Exception {
        Users user = Users.builder()
                .id(1L)
                .email("test1@test.com")
                .password("test1").build();

        Album album1 = Album.builder().users(user)
                .id(1L)
                .idSpotify("123")
                .name("test1 album")
                .artistName("test1 artist")
                .imageUrl("test1 url")
                .value(BigDecimal.valueOf(97.10))
                .build();

        Album album2 = Album.builder().users(user)
                .id(2L)
                .idSpotify("1234")
                .name("test2 album")
                .artistName("test2 artist")
                .imageUrl("test2 url")
                .value(BigDecimal.valueOf(97.10))
                .build();

        albumService.saveAlbum(album1);
        albumService.saveAlbum(album2);

        List<Album> albumList = Arrays.asList(album1, album2);

        when(albumService.getCollection(user)).thenReturn(albumList);

        assertEquals(2, albumList.size());
    }

    @Test
    @DisplayName("Should remove album when ID is valid")
    public void ShouldRemoveAlbumWhenIDIsValid() throws Exception {

        Users user = Users.builder()
                .id(1L)
                .email("test1@test.com")
                .password("test1").build();

        Album album1 = Album.builder().users(user)
                .id(1L)
                .idSpotify("123")
                .name("test album")
                .artistName("test artist")
                .imageUrl("test url")
                .value(BigDecimal.valueOf(97.10))
                .build();

        doNothing().when(albumService).deleteAlbum(1L);

        mockMvc.perform(delete("/albums/remove/{id}", 1L))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Should return exception when ID is invalid")
    public void ShouldReturnExceptionWhenIDIsInvalid() throws Exception {
        Users user = Users.builder()
                .id(1L)
                .email("test1@test.com")
                .password("test1").build();

        Album album1 = Album.builder().users(user)
                .id(1L)
                .idSpotify("123")
                .name("test album")
                .artistName("test artist")
                .imageUrl("test url")
                .value(BigDecimal.valueOf(97.10))
                .build();

        doThrow(EntityNotFoundException.class).when(albumService).deleteAlbum(1L);

        mockMvc.perform(delete("/albums/{id}", 2L))
                .andExpect(status().isNotFound());
    }
}