package com.curso.seguridad.repositories;


import com.curso.seguridad.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.user_identify LIKE :idUser")
    List<User> getUser(@Param("idUser") String idUser);


}

