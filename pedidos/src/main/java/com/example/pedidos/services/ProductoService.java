package com.example.pedidos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.pedidos.model.Producto;
import com.example.pedidos.model.TipoProducto;
import com.example.pedidos.repository.ProductoRepository;

import lombok.*;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository repo;

    public Producto crear(Producto p) {
         Optional<Producto> existente = repo.findByNombre(p.getNombre());
    if (existente.isPresent()) {
        throw new RuntimeException("Producto con ese nombre ya existe");
    }
        return repo.save(p);
    }

    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    public List<Producto> filtrarPorTipo(TipoProducto tipo) {
        return repo.findByTipo(tipo);
    }

    public Producto buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto not found with id: " + id));
    }

}
