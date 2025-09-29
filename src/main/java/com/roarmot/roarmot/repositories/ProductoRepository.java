package com.roarmot.roarmot.repositories;

import com.roarmot.roarmot.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List; // Importar List para el nuevo método

/**
 * Interfaz de Repositorio para la entidad Producto.
 * Extiende JpaRepository, proporcionando métodos CRUD listos para usar.
 */
@Repository // Indica a Spring que esta interfaz es un repositorio
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Aquí puedes agregar métodos personalizados de búsqueda si los necesitas.
    // Ejemplo: List<Producto> findByNombreContaining(String nombre);

     /**
     * Busca todos los productos que pertenecen a un usuario específico.
     * La convención de nombre es: findBy[NombreCampoRelacion]_[NombreCampoID].
     * En este caso: findByUsuario_IdUsuario.
     * Se asume que en el modelo Usuario, el campo ID se llama 'idUsuario'
     */
    List<Producto> findByUsuario_IdUsuario(Long idUsuario);
}