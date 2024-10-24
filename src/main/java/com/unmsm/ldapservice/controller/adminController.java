package com.unmsm.ldapservice.controller;

import com.unmsm.ldapservice.helper.Utils;
import com.unmsm.ldapservice.model.Search;
import com.unmsm.ldapservice.model.Usuario;
import com.unmsm.ldapservice.model.cambioBody;
import com.unmsm.ldapservice.service.CambioClaveGoogle;
import com.unmsm.ldapservice.service.CambioClaveLdap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("api/admin")
public class adminController {
    private static final Logger log = LoggerFactory.getLogger(adminController.class);
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

    @GetMapping(path = "/exists/{email}")
    public ResponseEntity<Search> userExists(@PathVariable("email") String email) throws Exception {
        Search res = new Search();
        res.setGoogle(google.obtenerUsuario(email+"@unmsm.edu.pe"));
        res.setLdap(ldap.Buscar(email));
        return  ResponseEntity.ok(res);
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
    public ResponseEntity<String> getFacultad(@PathVariable("facu")String facu){
        Utils util = new Utils();
        String res = util.buscarfacultad(facu);
        if(res != null){
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(path = "/cambioldap")
        public ResponseEntity<String> cambioLdap(@RequestBody Usuario usua) throws Exception {
        int cont = this.ldap.Buscar(usua.getCorreo_sm());
        if(cont == 0){
            return ResponseEntity.notFound().build();
        }
        try {
            this.ldap.cambioClave(usua.getCorreo_sm(), usua.getPass());
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping(path = "/estudiante")
    public ResponseEntity<String> crearEstudiante(@RequestBody Usuario usua) {
        Utils util = new Utils();
        String Status;
        String sEmail = usua.getCorreo_sm() + "@unmsm.edu.pe";
        String apellidos = usua.getApellido_paterno() + " " + usua.getApellido_materno();

        //uidNumber = id de usuario en db
        //uid = correo sin dominio
        //stipo = tipo + facultad ejem: pregrado + medicina

        String facu = util.buscarfacultad(usua.getDesc_facu());
        System.out.println(usua.getDesc_facu());
        System.out.println(facu);

        if(facu == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error en la facultad");
        }
        String Stipo = usua.getDesc_tipo_usua() + " " + facu;
        String sGrupo;
        //System.out.println(Stipo);
        String situacion = usua.getSituacion();

        if(situacion == "2"){
            sGrupo = "EGRESADOS";
        }
        else if(usua.getDesc_tipo_usua().equals("Pregrado")){
            sGrupo = "PREGRADOM";
        }
        else if(usua.getDesc_tipo_usua().equals("Posgrado")){
            sGrupo = "POSGRADOM";
        }
        else{
            sGrupo = "Users";
        }
        //System.out.println(sGrupo);
        //log.info(sGrupo);

        try {
            String usuario = this.google.obtenerUsuario(usua.getCorreo_sm() + "@unmsm.edu.pe");
            if (usuario == null) {
                this.ldap.agregarUsuario(usua.getCorreo_sm(), usua.getUidNumber(), "0", usua.getCod_usua(), apellidos,
                        usua.getNombres(), sEmail, usua.getNum_doc(), usua.getDesc_facu(), Stipo, usua.getPass());
                //this.ldap.cambioClave(usua.getCorreo_sm(), usua.getPass());
                this.google.agregarUsuario(apellidos, usua.getNombres(), sEmail, usua.getPass(), sGrupo);
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
    public ResponseEntity<String> crearDocente(@RequestBody Usuario usua) {
        String sEmail = usua.getCorreo_sm() + "@unmsm.edu.pe";
        Utils util = new Utils();
        String Status;
        String apellidos = usua.getApellido_paterno() + " " + usua.getApellido_materno();
        String facu = util.buscarfacultad(usua.getDesc_facu());
        String tipo = "Docente";

        if(facu == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error en la facultad");
        }

        try {
            String usuario = this.google.obtenerUsuario(sEmail);
            if (usuario == null) {
                this.ldap.agregarUsuarioDocente(1, "0", usua.getCorreo_sm(), usua.getUidNumber(), "0", usua.getCod_usua(), apellidos,
                            usua.getNombres(), sEmail, usua.getPass(), usua.getDesc_facu(),
                        tipo + " " + facu, tipo.toUpperCase());
                //this.ldap.cambioClave(usua.getCorreo_sm(), usua.getPass());

                this.google.agregarUsuario(apellidos, usua.getNombres(), sEmail, usua.getPass(), "Docentes");
                Status = "El usuario " + sEmail +" fue creado";
                return ResponseEntity.ok(Status);
            } else {
                Status = "El usuario " + sEmail +" ya existe";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Status);
            }
        } catch (Exception ex) {
            Status = "Error: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Status);
        } catch (Throwable ex) {
            Status = "Error: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Status);
        }
    }

    @PostMapping(path = "/cambioclave")
    public ResponseEntity<String> cambiarClave(@RequestBody cambioBody user) throws Exception{
        String sError;
        String uString;
        String uName = user.getUsername();
        String oPass = user.getOldPass();
        String uPass = user.getPass();

        if(uName== null || oPass == null || uPass == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("datos incompletos");
        }

        try {
            uString = this.google.obtenerUsuario(uName+"@unmsm.edu.pe");
            if(uString == null){
                return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado en google");
            }
        }catch (Exception e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al llamar al servicio de google");
        }

        try {
            sError = this.ldap.verificaClave(uName, oPass);
            if(sError.isEmpty()){
                this.ldap.cambioClave(uName, uPass);
                this.google.cambioClave(uName+"@unmsm.edu.pe", uPass);
                return ResponseEntity.ok("Cambio de clave exitoso");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sError);
        }catch (Exception ex){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar cambiar la contrasena");

        } catch (Throwable e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al llamar al servicio de google");
        }

    }

    @PostMapping(path = "/crearGoogleDocente")
    public ResponseEntity<String> crearGoogleDocente(@RequestBody Usuario usua) throws Exception{
        String sEmail = usua.getCorreo_sm() + "@unmsm.edu.pe";
        String apellidos = usua.getApellido_paterno() + " " + usua.getApellido_materno();
        String uString;
        try{
            uString = this.google.obtenerUsuario(sEmail);
            if(uString == null){
                this.google.agregarUsuario(apellidos, usua.getNombres(), sEmail, usua.getPass(), "Docentes");
                return ResponseEntity.status(HttpStatus.OK).body("Usuario creado exitosamente");
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario ya existe");
            }
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el usuario");
        }
    }

    @PostMapping(path = "/crearGoogleAlumno")
    public ResponseEntity<String> crearGoogleAlumno(@RequestBody Usuario usua) throws Exception{
        String sEmail = usua.getCorreo_sm() + "@unmsm.edu.pe";
        String apellidos = usua.getApellido_paterno() + " " + usua.getApellido_materno();
        String uString;
        String situacion = usua.getSituacion();
        String sGrupo;

        if(situacion == "2"){
            sGrupo = "EGRESADOS";
        }
        else if(usua.getDesc_tipo_usua().equals("Pregrado")){
            sGrupo = "PREGRADOM";
        }
        else if(usua.getDesc_tipo_usua().equals("Posgrado")){
            sGrupo = "POSGRADOM";
        }
        else{
            sGrupo = "Users";
        }
        try{
            uString = this.google.obtenerUsuario(sEmail);
            if(uString == null){
                this.google.agregarUsuario(apellidos, usua.getNombres(), sEmail, usua.getPass(), sGrupo);
                return ResponseEntity.status(HttpStatus.OK).body("Usuario creado exitosamente");
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario ya existe");
            }
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el usuario");
        }
    }

    @PostMapping(path = "/crearldapdocente")
    public ResponseEntity<String> crearLdapDocente(@RequestBody Usuario usua) {
        String sEmail = usua.getCorreo_sm() + "@unmsm.edu.pe";
        Utils util = new Utils();
        String Status;
        String apellidos = usua.getApellido_paterno() + " " + usua.getApellido_materno();
        String facu = util.buscarfacultad(usua.getDesc_facu());
        String tipo = "Docente";

        if(facu == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error en la facultad");
        }

        try {
            int usuario = this.ldap.Buscar(usua.getCorreo_sm());
            if (usuario < 1) {
                this.ldap.agregarUsuarioDocente(1, "0", usua.getCorreo_sm(), usua.getUidNumber(), "0", usua.getCod_usua(), apellidos,
                        usua.getNombres(), sEmail, usua.getPass(), usua.getDesc_facu(),
                        tipo + " " + facu, tipo.toUpperCase());
                //this.ldap.cambioClave(usua.getCorreo_sm(), usua.getPass());

                Status = "El usuario " + sEmail +" fue creado";
                return ResponseEntity.ok(Status);
            } else {
                Status = "El usuario " + sEmail +" ya existe";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Status);
            }
        } catch (Throwable ex) {
            Status = "Error: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Status);
        }
    }

    @PostMapping(path = "/crearldapestudiante")
    public ResponseEntity<String> crearLdapEstudiante(@RequestBody Usuario usua) {
        Utils util = new Utils();
        String Status;
        String sEmail = usua.getCorreo_sm() + "@unmsm.edu.pe";
        String apellidos = usua.getApellido_paterno() + " " + usua.getApellido_materno();

        String facu = util.buscarfacultad(usua.getDesc_facu());

        if(facu == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error en la facultad");
        }
        String Stipo = usua.getDesc_tipo_usua() + " " + facu;


        try {
            String usuario = this.google.obtenerUsuario(usua.getCorreo_sm() + "@unmsm.edu.pe");
            if (usuario == null) {
                this.ldap.agregarUsuario(usua.getCorreo_sm(), usua.getUidNumber(), "0", usua.getCod_usua(), apellidos,
                        usua.getNombres(), sEmail, usua.getNum_doc(), usua.getDesc_facu(), Stipo, usua.getPass());

                Status = "El usuario " + sEmail +" fue creado";
                return ResponseEntity.ok(Status);
            } else {
                Status = "El usuario " + sEmail +" ya existe";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Status);
            }
        } catch (Throwable e) {
            Status = "Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Status);
        }
    }

}
