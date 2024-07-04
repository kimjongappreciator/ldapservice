package com.unmsm.ldapservice.model;

import lombok.Data;

@Data
public class Usuario {
    private String num_doc;
    private String cod_usua;
    private String correo_sm;
    private String nombres;
    private String apellido_paterno;
    private String apellido_materno;
    private String desc_facu;
    private String desc_tipo_usua;
    private String situacion;
    private String grupo;
    private String codigo;
    private String UidNumber;
    private String pass;
}
