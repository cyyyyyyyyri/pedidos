package com.example.pedidos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pedidos.dto.ProductoDTO;
import com.example.pedidos.model.EstadoPedido;
import com.example.pedidos.model.PedidoDetalle;
import com.example.pedidos.model.Ppedido;
import com.example.pedidos.repository.PedidoRepository;
//import com.example.pedidos.services.ProductoService;
@Service
public class PedidoService {
    @Autowired
    private PedidoRepository repo;

    @Autowired
    private ProductoService productoService;

    public Ppedido crearPedido(Ppedido pedido) {
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.NUEVO);

        // Iterar sobre los detalles del pedido para obtener los productos asociados
        for (PedidoDetalle detalle : pedido.getDetalles()) {
            ProductoDTO productoDTO = productoService.obtenerProductoDTO(detalle.getProducto().getId());
            // Aquí puedes realizar cualquier procesamiento adicional con el productoDTO
        }

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
        if (!repo.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: pedido con id " + id + " no existe");
        }
        repo.deleteById(id);  // Si el pedido existe, lo eliminamos
    }
}