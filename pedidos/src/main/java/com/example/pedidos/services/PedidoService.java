package com.example.pedidos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pedidos.model.EstadoPedido;
import com.example.pedidos.model.Ppedido;
import com.example.pedidos.repository.PedidoRepository;

@Service
public class PedidoService {
     @Autowired
    private PedidoRepository repo;

    public Ppedido crearPedido(Ppedido pedido) {
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.NUEVO);
        return repo.save(pedido);
    }

    public Optional<Ppedido> findById(Long id) {
        return repo.findById(id);
    }

    public List<Ppedido> findAll() {
        return repo.findAll();
    }

    public Ppedido confirmarPedido(Long id) {
        return actualizarEstado(id, EstadoPedido.CONFIRMADO);
    }

    public Ppedido procesarPedido(Long id) {
        return actualizarEstado(id, EstadoPedido.EN_PROCESO);
    }

    public Ppedido actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Ppedido p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id " + id));
        p.setEstado(nuevoEstado);
        return repo.save(p);
    }
     public void eliminarPedido(Long id) {
        // Verifica que exista antes de intentar borrar
        if (!repo.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: pedido con id " + id + " no existe");
        }
        repo.deleteById(id);
    }


}
