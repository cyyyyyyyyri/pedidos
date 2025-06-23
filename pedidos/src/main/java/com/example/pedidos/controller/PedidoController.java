package com.example.pedidos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pedidos.model.EstadoPedido;
import com.example.pedidos.model.Ppedido;
import com.example.pedidos.services.PedidoService;

@RestController
@RequestMapping("api/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Ppedido> crearPedido(@RequestBody Ppedido pedido) {
        Ppedido creado = pedidoService.crearPedido(pedido);
        return ResponseEntity
                .created(URI.create("/api/pedidos/" + creado.getId()))
                .body(creado);
    }

    @GetMapping
    public ResponseEntity<List<Ppedido>> listarPedidos() {
        List<Ppedido> lista = pedidoService.findAll();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ppedido> getPedido(@PathVariable Long id) {
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Ppedido> confirmarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.confirmarPedido(id));
    }

    @PostMapping("/{id}/procesar")
    public ResponseEntity<Ppedido> procesarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.procesarPedido(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Ppedido> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            // Si se eliminó correctamente, devolvemos 204 No Content
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            // Si el pedido no existía, devolvemos 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    

}
