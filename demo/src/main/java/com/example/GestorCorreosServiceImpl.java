package com.example;

import io.grpc.stub.StreamObserver;
import com.example.GestorCorreosServiceProto.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GestorCorreosServiceImpl extends GestorCorreosServiceGrpc.GestorCorreosServiceImplBase {

    private final Map<String, List<Correo>> bandejaDeEntrada = new HashMap<>();
    private final Map<String, GrupoDeUsuarios> gruposDeUsuarios = new HashMap<>();
    private final List<String> usuariosRegistrados = List.of(
            "usuario@maquina_virtual.com",
            "usuario1@maquina_virtual.com",
            "usuario2@maquina_virtual.com",
            "usuario3@maquina_virtual.com"
    );

    public GestorCorreosServiceImpl() {
        GrupoDeUsuarios grupo1 = new GrupoDeUsuarios("grupo1");
        grupo1.agregarContacto("usuario1@example.com");
        grupo1.agregarContacto("usuario2@example.com");

        GrupoDeUsuarios grupo2 = new GrupoDeUsuarios("grupo2");
        grupo2.agregarContacto("usuario3@example.com");

        gruposDeUsuarios.put(grupo1.getNombre(), grupo1);
        gruposDeUsuarios.put(grupo2.getNombre(), grupo2);
    }

    private boolean usuarioExiste(String email) {
        return usuariosRegistrados.contains(email);
    }
    
    @Override
    public void enviarCorreo(CorreoRequest request, StreamObserver<CorreoResponse> responseObserver) {
        Correo correo = request.getCorreo();
        String destinatario = correo.getDestinatario();
        boolean correoEnviadoCorrectamente = false;
        String mensajeRespuesta;
    
        // Verificar si el destinatario existe
        if (!usuarioExiste(destinatario) && !gruposDeUsuarios.containsKey(destinatario)) {
            mensajeRespuesta = "Error: El destinatario " + destinatario + " no existe.";
            CorreoResponse response = CorreoResponse.newBuilder()
                    .setExito(false)
                    .setMensaje(mensajeRespuesta)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
    
        // Enviar correo a un grupo
        if (gruposDeUsuarios.containsKey(destinatario)) {
            GrupoDeUsuarios grupo = gruposDeUsuarios.get(destinatario);
    
            for (String miembro : grupo.getContactos()) {
                if (!miembro.equals(correo.getRemitente()) && usuarioExiste(miembro)) {
                    // Agregar el correo a la bandeja de entrada del miembro
                    bandejaDeEntrada.computeIfAbsent(miembro, k -> new ArrayList<>()).add(correo);
                    correoEnviadoCorrectamente = true;
    
                    // Imprimir acceso a la bandeja del miembro en la terminal
                    System.out.println("Acceso a la bandeja de entrada de " + miembro + ": Correo recibido de " + correo.getRemitente());
                }
            }
            mensajeRespuesta = correoEnviadoCorrectamente
                    ? "Correo enviado al grupo " + destinatario
                    : "Error al enviar correo al grupo.";
        } else {
            // Enviar correo individual
            bandejaDeEntrada.computeIfAbsent(destinatario, k -> new ArrayList<>()).add(correo);
            correoEnviadoCorrectamente = true;
    
            // Imprimir acceso a la bandeja del destinatario en la terminal
            System.out.println("Acceso a la bandeja de entrada de " + destinatario + ": Correo recibido de " + correo.getRemitente());
            mensajeRespuesta = "Correo enviado a " + destinatario;
        }
    
        CorreoResponse response = CorreoResponse.newBuilder()
                .setExito(correoEnviadoCorrectamente)
                .setMensaje(mensajeRespuesta)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public void recibirCorreo(CorreoRequest request, StreamObserver<CorreoResponse> responseObserver) {
        Correo correoSolicitado = request.getCorreo();
        List<Correo> correosDelDestinatario = bandejaDeEntrada.get(correoSolicitado.getDestinatario());
        Correo encontrado = null;

        if (correosDelDestinatario != null) {
            for (Correo correo : correosDelDestinatario) {
                if (correo.getRemitente().equals(correoSolicitado.getRemitente())) {
                    encontrado = correo;
                    break;
                }
            }
        }

        String mensajeRespuesta = (encontrado != null)
                ? "Correo de " + encontrado.getRemitente() +
                  "\nAsunto: " + encontrado.getAsunto() +
                  "\nContenido: " + encontrado.getContenido()
                : "No se encontró ningún correo del remitente " + correoSolicitado.getRemitente();

        CorreoResponse response = CorreoResponse.newBuilder()
                .setMensaje(mensajeRespuesta)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void crearGrupo(GrupoRequest request, StreamObserver<GrupoResponse> responseObserver) {
        String nombreGrupo = request.getNombreGrupo();

        if (gruposDeUsuarios.containsKey(nombreGrupo)) {
            GrupoResponse response = GrupoResponse.newBuilder()
                    .setMensaje("El grupo " + nombreGrupo + " ya existe.")
                    .setExito(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        GrupoDeUsuarios nuevoGrupo = new GrupoDeUsuarios(nombreGrupo);
        for (String miembro : request.getMiembrosList()) {
            if (usuarioExiste(miembro)) {
                nuevoGrupo.agregarContacto(miembro);
            }
        }
        gruposDeUsuarios.put(nombreGrupo, nuevoGrupo);

        GrupoResponse response = GrupoResponse.newBuilder()
                .setMensaje("Grupo " + nombreGrupo + " creado exitosamente.")
                .setExito(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void marcarComoFavorito(FavoritoRequest request, StreamObserver<CorreoResponse> responseObserver) {
        String remitente = request.getRemitente();
        boolean esFavorito = request.getEsFavorito();
        Correo correoEncontrado = null;

        for (List<Correo> correos : bandejaDeEntrada.values()) {
            for (Correo c : correos) {
                if (c.getRemitente().equals(remitente)) {
                    correoEncontrado = c.toBuilder().setEsFavorito(esFavorito).build();
                    correos.set(correos.indexOf(c), correoEncontrado);
                    break;
                }
            }
            if (correoEncontrado != null) {
                break;
            }
        }

        String mensajeRespuesta = (correoEncontrado != null)
                ? "El correo de " + remitente + " ha sido " + (esFavorito ? "marcado como favorito." : "quitado de favoritos.")
                : "No se encontró el correo del remitente " + remitente;

        CorreoResponse response = CorreoResponse.newBuilder()
                .setMensaje(mensajeRespuesta)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listarFavoritos(FavoritoListRequest request, StreamObserver<FavoritoListResponse> responseObserver) {
        List<Correo> favoritos = new ArrayList<>();
        for (List<Correo> correos : bandejaDeEntrada.values()) {
            for (Correo correo : correos) {
                if (correo.getEsFavorito()) {
                    favoritos.add(correo);
                }
            }
        }

        FavoritoListResponse response = FavoritoListResponse.newBuilder()
                .addAllCorreosFavoritos(favoritos)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
