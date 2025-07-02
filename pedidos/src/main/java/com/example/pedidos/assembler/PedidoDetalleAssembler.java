package com.example.pedidos.assembler;

import com.example.pedidos.controller.PedidoController;
import com.example.pedidos.model.PedidoDetalle;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PedidoDetalleAssembler implements RepresentationModelAssembler<PedidoDetalle, EntityModel<PedidoDetalle>> {

    @Override
    public EntityModel<PedidoDetalle> toModel(PedidoDetalle detalle) {
        return EntityModel.of(detalle,
            // Enlace al detalle específico
            linkTo(methodOn(PedidoController.class).getPedido(detalle.getPedido().getId())).withSelfRel(),
            // Enlace para listar todos los pedidos
            linkTo(methodOn(PedidoController.class).listarPedidos()).withRel("detalles")
        );
    }
}
