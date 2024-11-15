package com.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.GestorCorreosServiceProto.*;
import com.example.GestorCorreosServiceGrpc.GestorCorreosServiceBlockingStub;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReceptorDeEnvioMP {

    private final GestorCorreosServiceBlockingStub client;

    private static final List<String> GRUPO_DESTINATARIOS = Arrays.asList(
            "usuario1@maquina_virtual.com",
            "usuario2@maquina_virtual.com",
            "usuario3@maquina_virtual.com"
    );

    public ReceptorDeEnvioMP(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        client = GestorCorreosServiceGrpc.newBlockingStub(channel);
    }

    public void recibirCorreo(String destinatario, String remitente) {
        CorreoRequest request = CorreoRequest.newBuilder()
                .setCorreo(Correo.newBuilder()
                        .setDestinatario(destinatario)
                        .setRemitente(remitente)
                        .build())
                .build();

        CorreoResponse response = client.recibirCorreo(request);

        // Verificar si el destinatario está en el grupo
        if (GRUPO_DESTINATARIOS.contains(destinatario)) {
            System.out.println("Correo recibido por " + destinatario + ": " + response.getMensaje());

            // Marcar el correo como favorito
            FavoritoRequest marcarFavoritoRequest = FavoritoRequest.newBuilder()
                    .setRemitente(remitente)
                    .setEsFavorito(true)
                    .build();
            CorreoResponse responseFavorito = client.marcarComoFavorito(marcarFavoritoRequest);
            System.out.println("Correo marcado como favorito: " + responseFavorito.getMensaje());

            // Desmarcar el correo como favorito
            FavoritoRequest desmarcarFavoritoRequest = FavoritoRequest.newBuilder()
                    .setRemitente(remitente)
                    .setEsFavorito(false)
                    .build();
            CorreoResponse responseDesmarcarFavorito = client.marcarComoFavorito(desmarcarFavoritoRequest);
            System.out.println("Correo desmarcado de favorito: " + responseDesmarcarFavorito.getMensaje());

        } else {
            System.out.println("El destinatario " + destinatario + " no pertenece al grupo y no recibió el correo.");
        }
    }

    public static void main(String[] args) {
        String host = "192.168.0.249";  // IP de la VM
        int port = 50051;  // Puerto del servidor

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                                                      .usePlaintext()
                                                      .build();
        try {
            ReceptorDeEnvioMP client = new ReceptorDeEnvioMP(host, port);

            // Prueba con destinatarios en el grupo
            for (String destinatario : GRUPO_DESTINATARIOS) {
                client.recibirCorreo(destinatario, "usuario_remitente@dominio.com");
            }

            // Prueba con un destinatario fuera del grupo
            client.recibirCorreo("usuario_fuera_grupo@maquina_virtual.com", "usuario_remitente@dominio.com");

        } finally {
            if (channel != null) {
                try {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

