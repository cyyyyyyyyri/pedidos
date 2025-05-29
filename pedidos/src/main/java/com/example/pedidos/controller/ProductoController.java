package com.example.pedidos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pedidos.model.Producto;
import com.example.pedidos.model.TipoProducto;
import com.example.pedidos.services.ProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService svc;

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto p) {
        Producto guardado = svc.crear(p);
        return ResponseEntity
            .created(URI.create("/api/productos/" + guardado.getId()))
            .body(guardado);
    }
    @GetMapping
    public ResponseEntity<List<Producto>> listar(
        @RequestParam(value = "tipo", required = false) TipoProducto tipo) {

        List<Producto> lista = (tipo != null)
            ? svc.filtrarPorTipo(tipo)
            : svc.listarTodos();

        return lista.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(lista);
    }

     @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(svc.buscarPorId(id));
    }

}
