package com.example.pedidos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import com.example.pedidos.assembler.PpedidoAssembler;
import com.example.pedidos.model.EstadoPedido;
import com.example.pedidos.model.Ppedido;
import com.example.pedidos.services.PedidoService;

@RestController
@RequestMapping("api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PpedidoAssembler ppedidoAssembler;

       @PostMapping
    public ResponseEntity<EntityModel<Ppedido>> crearPedido(@RequestBody Ppedido pedido) {
        Ppedido creado = pedidoService.crearPedido(pedido);
        EntityModel<Ppedido> resource = ppedidoAssembler.toModel(creado);
        return ResponseEntity
                .created(resource.getRequiredLink("self").toUri())
                .body(resource);
    }

       @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Ppedido>>> listarPedidos() {
        List<EntityModel<Ppedido>> list = pedidoService.findAll().stream()
            .map(ppedidoAssembler::toModel)
            .toList();
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(list,
            linkTo(methodOn(PedidoController.class).listarPedidos()).withSelfRel()
        ));
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Ppedido>> getPedido(@PathVariable Long id) {
        return pedidoService.findById(id)
            .map(ppedidoAssembler::toModel)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<EntityModel<Ppedido>> confirmarPedido(@PathVariable Long id) {
        Ppedido p = pedidoService.confirmarPedido(id);
        return ResponseEntity.ok(ppedidoAssembler.toModel(p));
    }

    @PostMapping("/{id}/procesar")
    public ResponseEntity<EntityModel<Ppedido>> procesarPedido(@PathVariable Long id) {
        Ppedido p = pedidoService.procesarPedido(id);
        return ResponseEntity.ok(ppedidoAssembler.toModel(p));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EntityModel<Ppedido>> actualizarEstado(@PathVariable Long id,
                                                                 @RequestParam EstadoPedido estado) {
        Ppedido p = pedidoService.actualizarEstado(id, estado);
        return ResponseEntity.ok(ppedidoAssembler.toModel(p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
