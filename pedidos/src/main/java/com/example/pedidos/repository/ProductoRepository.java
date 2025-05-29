package com.example.pedidos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pedidos.model.Producto;
import com.example.pedidos.model.TipoProducto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByTipo(TipoProducto tipo);

     Optional<Producto> findByNombre(String nombre);

}
