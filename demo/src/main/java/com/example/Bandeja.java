package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.example.GestorCorreosServiceProto.Correo;

public class Bandeja {
    private List<Correo> correosEnviados;
    private List<Correo> correosRecibidos;

    public Bandeja() {
        this.correosEnviados = new ArrayList<>();
        this.correosRecibidos = new ArrayList<>();
    }

    public void agregarCorreoEnviado(Correo correo) {
        correosEnviados.add(correo);
    }

    public void agregarCorreoRecibido(Correo correo) {
        correosRecibidos.add(correo);
    }

    public List<Correo> getCorreosEnviados() {
        return correosEnviados;
    }

    public List<Correo> getCorreosRecibidos() {
        return correosRecibidos;
    }

    // Filtros Normales
    public List<Correo> filtrarPorAsunto(String asunto) {
        return correosRecibidos.stream()
            .filter(c -> c.getAsunto().equalsIgnoreCase(asunto))
            .collect(Collectors.toList());
    }

    public List<Correo> filtrarPorRemitente(String remitente) {
        return correosRecibidos.stream()
            .filter(c -> c.getRemitente().equalsIgnoreCase(remitente))
            .collect(Collectors.toList());
    }

    public List<Correo> filtrarPorContenido(String contenido) {
        return correosRecibidos.stream()
            .filter(c -> c.getContenido().contains(contenido))
            .collect(Collectors.toList());
    }

    // Filtros Complejos
    public List<Correo> filtrarPorAsuntoYContenido(String asunto, String contenido) {
        return correosRecibidos.stream()
            .filter(c -> c.getAsunto().equalsIgnoreCase(asunto) && c.getContenido().contains(contenido))
            .collect(Collectors.toList());
    }

    public List<Correo> filtrarPorRemitenteYAsunto(String remitente, String asunto) {
        return correosRecibidos.stream()
            .filter(c -> c.getRemitente().equalsIgnoreCase(remitente) && c.getAsunto().equalsIgnoreCase(asunto))
            .collect(Collectors.toList());
    }
}

