package com.unmsm.ldapservice.controller;

import com.unmsm.ldapservice.helper.Utils;
import com.unmsm.ldapservice.model.Usuario;
import com.unmsm.ldapservice.service.CambioClaveGoogle;
import com.unmsm.ldapservice.service.CambioClaveLdap;
import jdk.jshell.execution.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class adminController {
    private final CambioClaveLdap ldap;
    private final CambioClaveGoogle google;

    public adminController() throws Exception {
        this.ldap= new CambioClaveLdap();
        this.google = new CambioClaveGoogle();
    }


    @GetMapping(path = "/usuario/{email}")
    public ResponseEntity<String> getUsuario(@PathVariable("email") String email) throws Exception {
        String user =google.obtenerUsuario(email);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping(path = "/grupos")
    public ResponseEntity<List<String>> getGrupos() throws Exception {
        List<String> grupos =ldap.obtenerGrupos();
        if(grupos == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(grupos);
    }

    @GetMapping(path = "/grupos/{id}")
    public ResponseEntity<String> getGruposAt(@PathVariable("id")Integer id) throws Exception {
        List<String> grupos =ldap.obtenerGrupos();
        if(grupos == null || id > grupos.size()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(grupos.get(id));
    }

    @GetMapping(path = "/facultad/{facu}")
    public ResponseEntity<String> getFacultad(@PathVariable("facu")String facu) throws Exception {
        Utils util = new Utils();
        String res = util.buscarfacultad(facu);
        if(res != null){
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping(path = "/estudiante")
    public ResponseEntity<String> crearEstudiante(@RequestBody Usuario usua) throws Exception {
        Utils util = new Utils();
        String Status;
        String sEmail = usua.getCorreo_sm() + "@unmsm.edu.pe";
        String apellidos = usua.getApellido_paterno() + usua.getApellido_materno();
        //uidNumber = id de usuario en db
        //uid = correo sin dominio
        //stipo = tipo + facultad ejem: pregrado + medicina
        String facu = util.buscarfacultad(usua.getDesc_facu());
        if(facu == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error en la facultad");
        }
        String Stipo = usua.getDesc_tipo_usua() + " " + facu;

        try {
            String usuario = this.google.obtenerUsuario(usua.getCorreo_sm() + "@unmsm.edu.pe");
            if (usuario == null) {
                this.ldap.agregarUsuario(usua.getCorreo_sm(), usua.getUidNumber(), "0", usua.getCodigo(), apellidos,
                        usua.getNombres(), sEmail, usua.getNum_doc(), usua.getDesc_facu(), Stipo);
                this.google.agregarUsuario(apellidos, usua.getNombres(), sEmail, usua.getNum_doc(), "Users");
                Status = "El usuario " + sEmail +" fue creado";
                return ResponseEntity.ok(Status);
            } else {
                Status = "El usuario " + sEmail +" ya existe";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Status);
            }
        } catch (Exception ex) {
            Status = "Error: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Status);
        } catch (Throwable e) {
            Status = "Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Status);
        }
    }

    @PostMapping(path = "/docente")
    public ResponseEntity<String> crearDocente() throws Exception {
        return ResponseEntity.ok("falta implementar");
    }



}
