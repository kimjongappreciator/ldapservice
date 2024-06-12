package com.unmsm.ldapservice.controller;

import com.google.api.services.admin.directory.model.User;
import com.unmsm.ldapservice.service.CambioClaveGoogle;
import com.unmsm.ldapservice.service.CambioClaveLdap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;

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

    @PostMapping(path = "/estudiante")
    public ResponseEntity<String> crearEstudiante() throws Exception {
        return ResponseEntity.ok("falta implementar");
    }

    @PostMapping(path = "/docente")
    public ResponseEntity<String> crearDocente() throws Exception {
        return ResponseEntity.ok("falta implementar");
    }



}
