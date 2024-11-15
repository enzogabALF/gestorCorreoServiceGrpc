package com.example;

import java.util.ArrayList;
import java.util.List;

public class GrupoDeUsuarios {
    private String nombre;
    private List<String> contactos;

    public GrupoDeUsuarios(String nombre) {
        this.nombre = nombre;
        this.contactos = new ArrayList<>();
    }

    public void agregarContacto(String contacto) {
        if (!contactos.contains(contacto)) {
            contactos.add(contacto);
            System.out.println(contacto + " agregado al grupo " + nombre);
        } else {
            System.out.println(contacto + " ya está en el grupo " + nombre);
        }
    }

    public void eliminarContacto(String contacto) {
        if (contactos.remove(contacto)) {
            System.out.println(contacto + " eliminado del grupo " + nombre);
        } else {
            System.out.println(contacto + " no se encuentra en el grupo " + nombre);
        }
    }

    public List<String> getContactos() {
        return contactos;
    }

    public String getNombre() {
        return nombre;
    }

    
    public boolean estaEnElGrupo(String contacto) {
        return contactos.contains(contacto);
    }
}
