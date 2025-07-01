package com.example.pedidos.controller;

import com.example.pedidos.model.EstadoPedido;
import com.example.pedidos.model.Ppedido;
import com.example.pedidos.services.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCrearPedido() throws Exception {
        Ppedido nuevo = new Ppedido();
        nuevo.setClienteId(100L);

        Ppedido guardado = new Ppedido();
        guardado.setId(1L);
        guardado.setClienteId(100L);
        guardado.setEstado(EstadoPedido.NUEVO);
        guardado.setFechaCreacion(LocalDateTime.now());

        Mockito.when(pedidoService.crearPedido(any(Ppedido.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/pedidos/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.clienteId").value(100L));
    }

    @Test
    void testListarPedidos_conContenido() throws Exception {
        Ppedido p1 = new Ppedido(1L, 100L, LocalDateTime.now(), EstadoPedido.NUEVO, null);
        Ppedido p2 = new Ppedido(2L, 200L, LocalDateTime.now(), EstadoPedido.EN_PROCESO, null);

        Mockito.when(pedidoService.findAll()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testListarPedidos_vacio() throws Exception {
        Mockito.when(pedidoService.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerPedidoPorId_existente() throws Exception {
        Ppedido pedido = new Ppedido(1L, 100L, LocalDateTime.now(), EstadoPedido.NUEVO, null);

        Mockito.when(pedidoService.findById(1L)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testObtenerPedidoPorId_noExistente() throws Exception {
        Mockito.when(pedidoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pedidos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testConfirmarPedido() throws Exception {
        Ppedido confirmado = new Ppedido(1L, 100L, LocalDateTime.now(), EstadoPedido.CONFIRMADO, null);
        Mockito.when(pedidoService.confirmarPedido(1L)).thenReturn(confirmado);

        mockMvc.perform(post("/api/pedidos/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    void testActualizarEstado() throws Exception {
        Ppedido actualizado = new Ppedido(1L, 100L, LocalDateTime.now(), EstadoPedido.EN_PROCESO, null);
        Mockito.when(pedidoService.actualizarEstado(1L, EstadoPedido.EN_PROCESO)).thenReturn(actualizado);

        mockMvc.perform(patch("/api/pedidos/1/estado")
                .param("estado", "EN_PROCESO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    @Test
    void testEliminarPedido_existente() throws Exception {
        Mockito.doNothing().when(pedidoService).eliminarPedido(1L);

        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isNoContent());
    }

   @Test
void testProcesarPedido_NoExiste() throws Exception {
    // Simulamos que el pedido no existe
    Mockito.when(pedidoService.procesarPedido(99L))
            .thenThrow(new RuntimeException("Pedido no encontrado"));

    mockMvc.perform(post("/api/pedidos/99/procesar"))
            .andExpect(status().isNotFound())  // Verificamos que se retorna 404
            .andExpect(content().string("Error: Pedido no encontrado"));  // Verificamos el mensaje de error
}
@Test
void testActualizarEstado_CasoInvalido() throws Exception {
    // Simulamos que el pedido no existe
    Mockito.when(pedidoService.actualizarEstado(99L, EstadoPedido.EN_PROCESO))
            .thenThrow(new RuntimeException("Pedido no encontrado"));

    mockMvc.perform(patch("/api/pedidos/99/estado")
            .param("estado", "EN_PROCESO"))
            .andExpect(status().isNotFound())  // Verificamos que se retorna 404
            .andExpect(content().string("Error: Pedido no encontrado"));  // Verificamos el mensaje de error
}
@Test
void testEliminarPedido_NoExiste() throws Exception {
    // Simulamos que el pedido no existe
    Mockito.doThrow(new RuntimeException("Pedido no encontrado")).when(pedidoService).eliminarPedido(99L);

    mockMvc.perform(delete("/api/pedidos/99"))
            .andExpect(status().isNotFound());  // Debería retornar 404 si el pedido no existe
}

}