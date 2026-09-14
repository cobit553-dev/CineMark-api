package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.Rol;
import com.devsv.cinemark_api.enums.NombreRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(NombreRol nombre);

    boolean existsByNombre(NombreRol nombre);
}