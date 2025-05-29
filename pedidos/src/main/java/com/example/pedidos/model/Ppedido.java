package com.example.pedidos.model;

import java.time.LocalDateTime;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ppedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    /**
     * IDs de los productos incluidos en el pedido.
     * Se mapea a una tabla auxiliar pedido_productos
     */

       @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PedidoDetalle> detalles;
}