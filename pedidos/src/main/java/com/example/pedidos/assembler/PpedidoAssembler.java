package com.example.pedidos.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.pedidos.controller.PedidoController;
import com.example.pedidos.model.Ppedido;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PpedidoAssembler implements RepresentationModelAssembler<Ppedido, EntityModel<Ppedido>> {

    @Override
    public EntityModel<Ppedido> toModel(Ppedido p) {
        return EntityModel.of(p,
            // Self
            linkTo(methodOn(PedidoController.class).getPedido(p.getId())).withSelfRel(),
            // Listado general
            linkTo(methodOn(PedidoController.class).listarPedidos()).withRel("pedidos"),
            // Confirmar
            linkTo(methodOn(PedidoController.class).confirmarPedido(p.getId())).withRel("confirmar"),
            // Procesar
            linkTo(methodOn(PedidoController.class).procesarPedido(p.getId())).withRel("procesar"),
            // Actualizar estado (mostramos un ejemplo con el mismo recurso)
            linkTo(methodOn(PedidoController.class).actualizarEstado(p.getId(), p.getEstado()))
                .withRel("actualizar-estado"),
            // Eliminar
            linkTo(methodOn(PedidoController.class).eliminarPedido(p.getId())).withRel("eliminar")
        );
    }
}