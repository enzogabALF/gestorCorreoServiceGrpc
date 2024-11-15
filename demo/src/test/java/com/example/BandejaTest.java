package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.GestorCorreosServiceProto.Correo;

public class BandejaTest {

    private Bandeja bandeja;

    @BeforeEach
    public void setUp() {
        bandeja = new Bandeja();
        
        // Agregar correos de prueba
        bandeja.agregarCorreoRecibido(Correo.newBuilder().setAsunto("Asunto1").setRemitente("Remitente1").setContenido("Contenido del primer correo").build());
        bandeja.agregarCorreoRecibido(Correo.newBuilder().setAsunto("Asunto2").setRemitente("Remitente2").setContenido("Este es el contenido del segundo correo").build());
        bandeja.agregarCorreoRecibido(Correo.newBuilder().setAsunto("Asunto3").setRemitente("Remitente1").setContenido("Otro contenido relacionado").build());
    }

    @Test
    public void testFiltrarPorAsunto() {
        List<Correo> resultado = bandeja.filtrarPorAsunto("Asunto1");
        assertEquals(1, resultado.size());
        assertEquals("Asunto1", resultado.get(0).getAsunto());
    }

    @Test
    public void testFiltrarPorRemitente() {
        List<Correo> resultado = bandeja.filtrarPorRemitente("Remitente1");
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(c -> c.getRemitente().equals("Remitente1")));
    }

    @Test
    public void testFiltrarPorContenido() {
        List<Correo> resultado = bandeja.filtrarPorContenido("primer correo");
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getContenido().contains("primer correo"));
    }

    @Test
    public void testFiltrarPorAsuntoYContenido() {
        List<Correo> resultado = bandeja.filtrarPorAsuntoYContenido("Asunto1", "primer correo");
        assertEquals(1, resultado.size());
    }

    @Test
    public void testFiltrarPorRemitenteYAsunto() {
        List<Correo> resultado = bandeja.filtrarPorRemitenteYAsunto("Remitente1", "Asunto3");
        assertEquals(1, resultado.size());
        assertEquals("Asunto3", resultado.get(0).getAsunto());
    }
}
