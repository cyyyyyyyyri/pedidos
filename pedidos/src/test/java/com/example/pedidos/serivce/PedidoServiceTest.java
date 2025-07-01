package com.example.pedidos.serivce;

import com.example.pedidos.model.EstadoPedido;
import com.example.pedidos.model.PedidoDetalle;
import com.example.pedidos.model.Ppedido;
import com.example.pedidos.repository.PedidoRepository;
import com.example.pedidos.services.PedidoService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository repo;

    @InjectMocks
    private PedidoService service;

    @Test
    void testCrearPedido() {
        Ppedido nuevo = new Ppedido();
        nuevo.setClienteId(99L);

        Ppedido guardado = new Ppedido();
        guardado.setId(1L);
        guardado.setClienteId(99L);
        guardado.setEstado(EstadoPedido.NUEVO);
        guardado.setFechaCreacion(LocalDateTime.now());

        when(repo.save(any(Ppedido.class))).thenReturn(guardado);

        Ppedido resultado = service.crearPedido(nuevo);

        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.NUEVO);
        assertThat(resultado.getFechaCreacion()).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repo).save(any(Ppedido.class));
    }

    @Test
    void testBuscarPorId() {
        Ppedido pedido = new Ppedido();
        pedido.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(pedido));

        Optional<Ppedido> resultado = service.findById(1L);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(repo).findById(1L);
    }

    @Test
    void testListarTodos() {
        Ppedido p1 = new Ppedido();
        Ppedido p2 = new Ppedido();

        when(repo.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Ppedido> resultado = service.findAll();
        assertThat(resultado).hasSize(2);
        verify(repo).findAll();
    }

    @Test
    void testConfirmarPedido() {
        Ppedido pedido = new Ppedido();
        pedido.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(pedido));
        when(repo.save(any(Ppedido.class))).thenAnswer(invoc -> invoc.getArgument(0));

        Ppedido resultado = service.confirmarPedido(1L);
        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.CONFIRMADO);
    }

    @Test
    void testActualizarEstado_NoExiste() {
        when(repo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.actualizarEstado(9L, EstadoPedido.CONFIRMADO));
    }

    @Test
    void testEliminarPedidoExiste() {
        when(repo.existsById(1L)).thenReturn(true);
        doNothing().when(repo).deleteById(1L);

        service.eliminarPedido(1L);
        verify(repo).deleteById(1L);
    }

   @Test
void testEliminarPedido() {
    Long idExistente = 1L;
    Long idInexistente = 999L;

    // Simula que el pedido con idExistente sí existe
    when(repo.existsById(idExistente)).thenReturn(true);
    doNothing().when(repo).deleteById(idExistente);

    // Simula que el pedido con idInexistente no existe
    when(repo.existsById(idInexistente)).thenReturn(false);

    // Verifica que un pedido que existe se elimina correctamente
    service.eliminarPedido(idExistente);
    verify(repo).deleteById(idExistente);

    // Verifica que un pedido inexistente lanza la excepción
    RuntimeException exception = assertThrows(RuntimeException.class, () -> service.eliminarPedido(idInexistente));
    assertThat(exception.getMessage()).contains("No se puede eliminar: pedido con id 999 no existe");
}
    @Test
void testProcesarPedido() {
    Ppedido pedido = new Ppedido(1L, 99L, LocalDateTime.now(), EstadoPedido.CONFIRMADO, null);

    when(repo.findById(1L)).thenReturn(Optional.of(pedido));
    when(repo.save(any(Ppedido.class))).thenAnswer(invoc -> invoc.getArgument(0));

    Ppedido resultado = service.procesarPedido(1L);
    assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.EN_PROCESO);
    verify(repo).findById(1L);
    verify(repo).save(pedido);
}
@Test
void testObtenerPedidoConDetalles() {
    PedidoDetalle detalle = PedidoDetalle.builder()
        .id(1L)
        .pedido(new Ppedido(1L, 99L, LocalDateTime.now(), EstadoPedido.NUEVO, null))
        .cantidad(5)
        .build();
    
    Set<PedidoDetalle> detalles = new HashSet<>(Arrays.asList(detalle));

    Ppedido pedido = new Ppedido(1L, 99L, LocalDateTime.now(), EstadoPedido.NUEVO, detalles);

    when(repo.findById(1L)).thenReturn(Optional.of(pedido));

    Optional<Ppedido> resultado = service.findById(1L);

    assertThat(resultado).isPresent();
    assertThat(resultado.get().getDetalles()).hasSize(1);  // Verifica que el pedido tiene 1 detalle
    verify(repo).findById(1L);
}

}