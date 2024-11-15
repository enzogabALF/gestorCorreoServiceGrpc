package com.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.GestorCorreosServiceProto.*;
import com.example.GestorCorreosServiceGrpc.GestorCorreosServiceBlockingStub;

import java.util.Arrays;
import java.util.List;

public class EnviarCorreoVM {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("192.168.0.249", 50051)
                .usePlaintext()
                .build();

        GestorCorreosServiceBlockingStub client = GestorCorreosServiceGrpc.newBlockingStub(channel);

        // Paso 1: Enviar correo individual
        Correo correoIndividual = Correo.newBuilder()
                .setAsunto("Correo de prueba individual")
                .setContenido("Este es un correo de prueba que se marcará y desmarcará como favorito.")
                .setRemitente("usuario_remitente@dominio.com")
                .setDestinatario("usuario@maquina_virtual.com")
                .build();

        CorreoRequest correoRequest = CorreoRequest.newBuilder().setCorreo(correoIndividual).build();
        CorreoResponse responseEnvio = client.enviarCorreo(correoRequest);
        System.out.println("Respuesta del envío individual: " + responseEnvio.getMensaje());

        // Paso 2: Crear un grupo de destinatarios y enviar el correo al grupo
        List<String> destinatariosGrupo = Arrays.asList(
                "usuario1@maquina_virtual.com",
                "usuario2@maquina_virtual.com",
                "usuario3@maquina_virtual.com"
        );

        // Crear el grupo
        GrupoRequest grupoRequest = GrupoRequest.newBuilder()
                .setNombreGrupo("Grupo de prueba")
                .addAllMiembros(destinatariosGrupo)
                .build();
        GrupoResponse grupoResponse = client.crearGrupo(grupoRequest);
        System.out.println("Grupo creado: " + grupoResponse.getMensaje());

        // Enviar el correo al grupo
        Correo correoGrupo = Correo.newBuilder()
                .setAsunto("Correo de prueba al grupo")
                .setContenido("Este es un correo de prueba para el grupo en la máquina virtual.")
                .setRemitente("usuario_remitente@dominio.com")
                .build();

        for (String destinatario : destinatariosGrupo) {
            CorreoRequest correoGrupoRequest = CorreoRequest.newBuilder()
                    .setCorreo(correoGrupo.toBuilder().setDestinatario(destinatario).build())
                    .build();

            CorreoResponse responseGrupo = client.enviarCorreo(correoGrupoRequest);
            System.out.println("Respuesta del servidor para " + destinatario + ": " + responseGrupo.getMensaje());
        }

        // Paso 3: Enviar un correo a un usuario inexistente
        Correo correoInexistente = Correo.newBuilder()
                .setAsunto("Correo a usuario inexistente")
                .setContenido("Este correo está destinado a un usuario que no existe.")
                .setRemitente("usuario_remitente@dominio.com")
                .setDestinatario("usuario_inexistente@dominio.com")
                .build();

        CorreoRequest correoInexistenteRequest = CorreoRequest.newBuilder().setCorreo(correoInexistente).build();
        CorreoResponse responseInexistente = client.enviarCorreo(correoInexistenteRequest);

        // Verificar si el destinatario existe basándose en la respuesta
        if (responseInexistente.getExito()) {
                System.out.println("Correo enviado exitosamente a usuario inexistente (esto no debería ocurrir)");
        } else {
                System.out.println("Error al enviar correo: " + responseInexistente.getMensaje());
        }



        // Cerrar el canal
        channel.shutdown();
    }
}
