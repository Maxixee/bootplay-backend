package br.com.sysmap.bootcamp.domain.respository;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findAllByUsers(Users users);

    boolean existsByIdSpotify(String idSpotify);

    boolean existsByUsersAndIdSpotify(Users users, String idSpotify);
}
