package com.unmsm.ldapservice.helper;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    //cambiar todo este metodo por un hashmap ejm (facultad de farmacia y bioquimica, Farmacia) donde (k, v) k es la llave y v es el valor de retorno
    public Utils(){
        this.facultad.add("Administracion");
        this.facultad.add("Biologia");
        this.facultad.add("Contabilidad");
        this.facultad.add("Derecho");
        this.facultad.add("Economia");
        this.facultad.add("Educacion");
        this.facultad.add("Electronica");
        this.facultad.add("Fisica");
        this.facultad.add("GMMG");
        this.facultad.add("Industrial");
        this.facultad.add("Letras");
        this.facultad.add("Matematica");
        this.facultad.add("Odontologia");
        this.facultad.add("Psicologia");
        this.facultad.add("Quimica");
        this.facultad.add("Sistemas");
        this.facultad.add("Sociales");
        this.facultad.add("Veterinaria");
        this.facultad.add("Medicina");
        this.facultad.add("Farmacia");

    }
    public List<String> facultad= new ArrayList<>();

    public String buscarfacultad(String nombre){
        for (String s : facultad) {
            String sLower = s.toLowerCase();
            String nombreLower = nombre.toLowerCase();
            if(nombreLower.equals("facultad de farmacia y bioquimica")){
                return "Farmacia";
            }
            else if(nombreLower.contains(sLower)){
                return s;
            }else if(nombreLower.contains("biologicas")){
                return "Biologia";
            }else if(nombreLower.contains("administrativas")){
                return "Administracion";
            }else if(nombreLower.contains("contables")){
                return "Contabilidad";
            }else if(nombreLower.contains("economicas")){
                return "Economia";
            }
        }return null;
    }
}
