package com.example.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pedidos.model.Ppedido;

@Repository
public interface PedidoRepository extends JpaRepository<Ppedido, Long> {

}
