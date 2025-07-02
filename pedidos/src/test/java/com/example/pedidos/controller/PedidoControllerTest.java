package com.example.pedidos.controller;

import com.example.pedidos.assembler.PpedidoAssembler;
import com.example.pedidos.model.EstadoPedido;
import com.example.pedidos.model.Ppedido;
import com.example.pedidos.services.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private PpedidoAssembler ppedidoAssembler;

    @Autowired
    private ObjectMapper objectMapper;

@Test
void testCrearPedido() throws Exception {
    // 1) Preparamos el pedido “guardado”
    Ppedido nuevo = Ppedido.builder().clienteId(100L).build();
    Ppedido guardado = Ppedido.builder()
            .id(1L)
            .clienteId(100L)
            .estado(EstadoPedido.NUEVO)
            .fechaCreacion(LocalDateTime.now())
            .build();

    // 2) Stub al servicio
    Mockito.when(pedidoService.crearPedido(any(Ppedido.class)))
           .thenReturn(guardado);

    // 3) Creamos el EntityModel que el controlador espera
    EntityModel<Ppedido> modelo = EntityModel.of(
        guardado,
        linkTo(methodOn(PedidoController.class).getPedido(guardado.getId())).withSelfRel()
    );
    // 4) Stub al assembler
    Mockito.when(ppedidoAssembler.toModel(guardado))
           .thenReturn(modelo);

    // 5) Ejecutamos el POST y verificamos 201 + Location + JSON
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
        Ppedido p1 = Ppedido.builder()
            .id(1L).clienteId(100L)
            .estado(EstadoPedido.NUEVO)
            .fechaCreacion(LocalDateTime.now())
            .build();
        Ppedido p2 = Ppedido.builder()
            .id(2L).clienteId(200L)
            .estado(EstadoPedido.EN_PROCESO)
            .fechaCreacion(LocalDateTime.now())
            .build();

        // Stub del servicio
        Mockito.when(pedidoService.findAll())
               .thenReturn(Arrays.asList(p1, p2));
        // Stub del assembler para cada elemento
        Mockito.when(ppedidoAssembler.toModel(p1))
               .thenReturn(EntityModel.of(p1));
        Mockito.when(ppedidoAssembler.toModel(p2))
               .thenReturn(EntityModel.of(p2));

        mockMvc.perform(get("/api/pedidos"))
            .andExpect(status().isOk())
            // La lista viene dentro de _embedded.ppedidoList
            .andExpect(jsonPath("$._embedded.ppedidoList.length()").value(2))
            .andExpect(jsonPath("$._embedded.ppedidoList[0].id").value(1))
            .andExpect(jsonPath("$._embedded.ppedidoList[1].id").value(2));
    }


    @Test
    void testListarPedidos_vacio() throws Exception {
        Mockito.when(pedidoService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pedidos"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerPedidoPorId_existente() throws Exception {
        Ppedido pedido = Ppedido.builder().id(1L).clienteId(100L).estado(EstadoPedido.NUEVO).fechaCreacion(LocalDateTime.now()).build();
        Mockito.when(pedidoService.findById(1L)).thenReturn(Optional.of(pedido));
        Mockito.when(ppedidoAssembler.toModel(pedido)).thenReturn(EntityModel.of(pedido));

        mockMvc.perform(get("/api/pedidos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.clienteId").value(100L))
            .andExpect(jsonPath("$.estado").value("NUEVO"));
    }

    @Test
    void testObtenerPedidoPorId_noExistente() throws Exception {
        Mockito.when(pedidoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pedidos/99"))
            .andExpect(status().isNotFound());
    }

 @Test
    void testConfirmarPedido() throws Exception {
        Ppedido confirmado = Ppedido.builder()
            .id(1L)
            .clienteId(100L)
            .estado(EstadoPedido.CONFIRMADO)
            .fechaCreacion(LocalDateTime.now())
            .build();

        // Stub del servicio
        Mockito.when(pedidoService.confirmarPedido(1L))
               .thenReturn(confirmado);
        // Stub del assembler para que devuelva un EntityModel válido
        Mockito.when(ppedidoAssembler.toModel(confirmado))
               .thenReturn(EntityModel.of(confirmado));

        mockMvc.perform(post("/api/pedidos/1/confirmar"))
            .andExpect(status().isOk())
            // ahora sí existe $.id y $.estado en el JSON raíz
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    void testProcesarPedido_existente() throws Exception {
        Ppedido proc = Ppedido.builder().id(1L).clienteId(100L).estado(EstadoPedido.EN_PROCESO).fechaCreacion(LocalDateTime.now()).build();
        Mockito.when(pedidoService.procesarPedido(1L)).thenReturn(proc);
        Mockito.when(ppedidoAssembler.toModel(proc)).thenReturn(EntityModel.of(proc));

        mockMvc.perform(post("/api/pedidos/1/procesar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    @Test
    void testProcesarPedido_NoExiste() throws Exception {
        Mockito.when(pedidoService.procesarPedido(99L))
               .thenThrow(new RuntimeException("Pedido no encontrado"));

        mockMvc.perform(post("/api/pedidos/99/procesar"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Error: Pedido no encontrado"));
    }

    @Test
    void testActualizarEstado() throws Exception {
        Ppedido actualizado = Ppedido.builder().id(1L).clienteId(100L).estado(EstadoPedido.EN_PROCESO).fechaCreacion(LocalDateTime.now()).build();
        Mockito.when(pedidoService.actualizarEstado(1L, EstadoPedido.EN_PROCESO)).thenReturn(actualizado);
        Mockito.when(ppedidoAssembler.toModel(actualizado)).thenReturn(EntityModel.of(actualizado));

        mockMvc.perform(patch("/api/pedidos/1/estado").param("estado", "EN_PROCESO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    @Test
    void testActualizarEstado_CasoInvalido() throws Exception {
        Mockito.when(pedidoService.actualizarEstado(99L, EstadoPedido.EN_PROCESO))
               .thenThrow(new RuntimeException("Pedido no encontrado"));

        mockMvc.perform(patch("/api/pedidos/99/estado").param("estado", "EN_PROCESO"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Error: Pedido no encontrado"));
    }

    @Test
    void testEliminarPedido_existente() throws Exception {
        Mockito.doNothing().when(pedidoService).eliminarPedido(1L);

        mockMvc.perform(delete("/api/pedidos/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarPedido_NoExiste() throws Exception {
        Mockito.doThrow(new RuntimeException("Pedido no encontrado")).when(pedidoService).eliminarPedido(99L);

        mockMvc.perform(delete("/api/pedidos/99"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Error: Pedido no encontrado"));
    }
}