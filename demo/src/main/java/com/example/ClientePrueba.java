package com.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.GestorCorreosServiceProto.*;
import com.example.GestorCorreosServiceGrpc.GestorCorreosServiceBlockingStub;

public class ClientePrueba {
    public static void main(String[] args) {
        // Crear canal de comunicación con el servidor
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                                                      .usePlaintext()
                                                      .build();

        // Crear un stub bloqueante para el servicio
        GestorCorreosServiceBlockingStub blockingStub = GestorCorreosServiceGrpc.newBlockingStub(channel);

        // Enviar un correo al grupo existente
        Correo correoGrupo = Correo.newBuilder()
            .setRemitente("usuario1@example.com")
            .setDestinatario("usuario2@example.com")
            .setAsunto("Asunto de prueba para grupo")
            .setContenido("Contenido del correo de prueba para el grupo")
            .build();

        CorreoRequest requestEnviarGrupo = CorreoRequest.newBuilder().setCorreo(correoGrupo).build();
        CorreoResponse responseEnviarGrupo = blockingStub.enviarCorreo(requestEnviarGrupo);
        System.out.println("Prueba de envío al grupo: " + responseEnviarGrupo.getMensaje());

        // Comprobación de recepción para los usuarios del grupo
        CorreoRequest requestRecibirUsuario2 = CorreoRequest.newBuilder()
            .setCorreo(Correo.newBuilder().setRemitente("usuario1@example.com").setDestinatario("usuario2@example.com").build())
            .build();
        CorreoResponse responseRecibirUsuario2 = blockingStub.recibirCorreo(requestRecibirUsuario2);
        System.out.println("Prueba de recibir correo para usuario2@example.com: " + responseRecibirUsuario2.getMensaje());

        CorreoRequest requestRecibirUsuario3 = CorreoRequest.newBuilder()
            .setCorreo(Correo.newBuilder().setRemitente("usuario1@example.com").setDestinatario("usuario3@example.com").build())
            .build();
        CorreoResponse responseRecibirUsuario3 = blockingStub.recibirCorreo(requestRecibirUsuario3);
        System.out.println("Prueba de recibir correo para usuario3@example.com: " + responseRecibirUsuario3.getMensaje());

        CorreoRequest requestRecibirUsuario4 = CorreoRequest.newBuilder()
            .setCorreo(Correo.newBuilder().setRemitente("usuario1@example.com").setDestinatario("usuario4@example.com").build())
            .build();
        CorreoResponse responseRecibirUsuario4 = blockingStub.recibirCorreo(requestRecibirUsuario4);
        System.out.println("Prueba de recibir correo para usuario4@example.com: " + responseRecibirUsuario4.getMensaje());

        // Enviar un correo directo
        Correo correoDirecto = Correo.newBuilder()
            .setRemitente("usuario1@example.com")
            .setDestinatario("usuario3@example.com")
            .setAsunto("Asunto de correo directo")
            .setContenido("Contenido del correo directo")
            .build();

        CorreoRequest requestEnviarDirecto = CorreoRequest.newBuilder().setCorreo(correoDirecto).build();
        CorreoResponse responseEnviarDirecto = blockingStub.enviarCorreo(requestEnviarDirecto);
        System.out.println("Prueba de envío directo: " + responseEnviarDirecto.getMensaje());

        // Comprobación de recepción para el correo directo
        CorreoRequest requestRecibirDirectoUsuario3 = CorreoRequest.newBuilder()
            .setCorreo(Correo.newBuilder().setRemitente("usuario1@example.com").setDestinatario("usuario3@example.com").build())
            .build();
        CorreoResponse responseRecibirDirectoUsuario3 = blockingStub.recibirCorreo(requestRecibirDirectoUsuario3);
        System.out.println("Prueba de recibir correo para usuario3@example.com: " + responseRecibirDirectoUsuario3.getMensaje());

        // Marcar y quitar correo como favorito
        FavoritoRequest requestMarcarFavorito = FavoritoRequest.newBuilder()
            .setRemitente("usuario1@example.com")
            .setEsFavorito(true)
            .build();
        CorreoResponse responseMarcarFavorito = blockingStub.marcarComoFavorito(requestMarcarFavorito);
        System.out.println("Prueba de marcar como favorito: " + responseMarcarFavorito.getMensaje());

        FavoritoRequest requestQuitarFavorito = FavoritoRequest.newBuilder()
            .setRemitente("usuario1@example.com")
            .setEsFavorito(false)
            .build();
        CorreoResponse responseQuitarFavorito = blockingStub.marcarComoFavorito(requestQuitarFavorito);
        System.out.println("Prueba de quitar de favoritos: " + responseQuitarFavorito.getMensaje());

        // Crear un nuevo grupo y agregar miembros
        GrupoRequest requestCrearGrupo = GrupoRequest.newBuilder()
        .setNombreGrupo("grupoNuevo")
        .addMiembros("usuario5@example.com")  // Agregar miembro al grupo
        .addMiembros("usuario6@example.com")  // Agregar otro miembro
        .build();

        GrupoResponse responseCrearGrupo = blockingStub.crearGrupo(requestCrearGrupo);
        System.out.println("Prueba de creación de nuevo grupo: " + responseCrearGrupo.getMensaje());

        // Enviar un correo al nuevo grupo
        Correo correoNuevoGrupo = Correo.newBuilder()
            .setRemitente("usuario5@example.com")
            .setDestinatario("grupoNuevo") // Usamos el nombre del grupo como destinatario
            .setAsunto("Asunto de prueba para nuevo grupo")
            .setContenido("Contenido del correo para el nuevo grupo")
            .build();

        CorreoRequest requestEnviarNuevoGrupo = CorreoRequest.newBuilder().setCorreo(correoNuevoGrupo).build();
        CorreoResponse responseEnviarNuevoGrupo = blockingStub.enviarCorreo(requestEnviarNuevoGrupo);
        System.out.println("Prueba de envío al nuevo grupo: " + responseEnviarNuevoGrupo.getMensaje());

        // Comprobar recepción del correo en los miembros del nuevo grupo
        CorreoRequest requestRecibirNuevoGrupoUsuario6 = CorreoRequest.newBuilder()
            .setCorreo(Correo.newBuilder().setRemitente("usuario5@example.com").setDestinatario("usuario6@example.com").build())
            .build();
        CorreoResponse responseRecibirNuevoGrupoUsuario6 = blockingStub.recibirCorreo(requestRecibirNuevoGrupoUsuario6);
        System.out.println("Prueba de recibir correo para usuario6@example.com en el nuevo grupo: " + responseRecibirNuevoGrupoUsuario6.getMensaje());

        // Comprobar que un usuario fuera del grupo no recibe el correo
        CorreoRequest requestRecibirFueraDelGrupo = CorreoRequest.newBuilder()
            .setCorreo(Correo.newBuilder().setRemitente("usuario5@example.com").setDestinatario("usuarioFueraGrupo@example.com").build())
            .build();
        CorreoResponse responseRecibirFueraDelGrupo = blockingStub.recibirCorreo(requestRecibirFueraDelGrupo);
        System.out.println("Prueba de recibir correo para usuarioFueraGrupo@example.com (fuera del grupo): " + responseRecibirFueraDelGrupo.getMensaje());


        // Cerrar el canal
        channel.shutdown();
    }
}