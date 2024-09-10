package com.unmsm.ldapservice.service;

import com.unmsm.ldapservice.helper.Claves;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class CambioClaveLdap {
    public DirContext ctx = null;

    public static final String S_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    public static final String S_PROVIDER_URL = "ldap://ldap.unmsm.edu.pe:389";

    public List<String> facultades = new ArrayList<>();

    public CambioClaveLdap() throws NamingException {
        this.ctx = conectar();
        this.facultades.add(0, null);
        this.facultades.add(1, "Administracion");
        this.facultades.add(2, "Biologia");
        this.facultades.add(3, "Contabilidad");
        this.facultades.add(4, "Derecho");
        this.facultades.add(5, "Economia");
        this.facultades.add(6, "Educacion");
        this.facultades.add(7, "Electronica");
        this.facultades.add(8, "Fisica");
        this.facultades.add(9, "GMMG");
        this.facultades.add(10, "Industrial");
        this.facultades.add(11, "Letras");
        this.facultades.add(12, "Matematica");
        this.facultades.add(13, "Odontologia");
        this.facultades.add(14, "Psicologia");
        this.facultades.add(15, "Quimica");
        this.facultades.add(16, "Sistemas");
        this.facultades.add(17, "Sociales");
        this.facultades.add(18, "Veterinaria");
        this.facultades.add(19, "Medicina");
        this.facultades.add(20, "Farmacia");
    }

    public DirContext conectar() throws NamingException {
        Hashtable<Object, Object> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("java.naming.provider.url", "ldap://ldap.unmsm.edu.pe:389");
        env.put("java.naming.security.principal", "cn=admin,dc=unmsm,dc=edu,dc=pe");
        env.put("java.naming.security.credentials", "wm..099");
        DirContext ctx = new InitialDirContext(env);
        return ctx;
    }

    public void agregarUsuario(String sUid, String sUidNumber, String sGidNumber, String sCodigo, String sSn, String sGivenName, String sEmail, String sDni, String sOu, String sTipo, String pass) throws Exception {
        String sNombres = sGivenName.trim() + " " + sSn.trim();
        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute("acctSyncWinSAMAccountName", sUid));
        matchAttrs.put(new BasicAttribute("cn", sNombres.toUpperCase()));
        matchAttrs.put(new BasicAttribute("gidNumber", sGidNumber));
        matchAttrs.put(new BasicAttribute("homeDirectory", "/var/spool/imap"));
        matchAttrs.put(new BasicAttribute("sn", sSn.toUpperCase()));
        matchAttrs.put(new BasicAttribute("uid", sUid));
        matchAttrs.put(new BasicAttribute("uidNumber", sUidNumber));
        matchAttrs.put(new BasicAttribute("acctSyncWinPassword", sCodigo));
        matchAttrs.put(new BasicAttribute("displayName", sNombres.toUpperCase()));
        matchAttrs.put(new BasicAttribute("givenName", sGivenName.toUpperCase()));
        matchAttrs.put(new BasicAttribute("mail", sEmail));
        matchAttrs.put(new BasicAttribute("o", mayuscula(sTipo)));
        matchAttrs.put(new BasicAttribute("st", Facultad(sOu)));
        matchAttrs.put(new BasicAttribute("userPassword", Claves.ldapMd5Password(pass)));
        BasicAttribute oc = new BasicAttribute("objectClass", "top");
        oc.add("inetOrgPerson");
        oc.add("acctSyncAccount");
        oc.add("posixAccount");
        matchAttrs.put(oc);
        this.ctx.createSubcontext("uid=" + sUid + ",ou=" + sTipo + ",ou=" + sOu + ",dc=unmsm,dc=edu,dc=pe", matchAttrs);
    }

    public void agregarUsuarioAdministrador(int lugar, String sUid, String sUidNumber, String sGidNumber, String sCodigo, String sSn, String sGivenName, String sEmail, String sDni, String sOu, String sOu1, String sTipo) throws Exception {
        String sNombres = sGivenName.trim() + " " + sSn.trim();
        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute("acctSyncWinSAMAccountName", sUid));
        matchAttrs.put(new BasicAttribute("cn", sNombres.toUpperCase()));
        matchAttrs.put(new BasicAttribute("gidNumber", sGidNumber));
        matchAttrs.put(new BasicAttribute("homeDirectory", "/var/spool/imap"));
        matchAttrs.put(new BasicAttribute("sn", sSn.toUpperCase()));
        matchAttrs.put(new BasicAttribute("uid", sUid));
        matchAttrs.put(new BasicAttribute("uidNumber", sUidNumber));
        matchAttrs.put(new BasicAttribute("acctSyncWinPassword", sCodigo));
        matchAttrs.put(new BasicAttribute("displayName", sNombres.toUpperCase()));
        matchAttrs.put(new BasicAttribute("givenName", sGivenName.toUpperCase()));
        matchAttrs.put(new BasicAttribute("mail", sEmail));
        matchAttrs.put(new BasicAttribute("o", sTipo));
        if (lugar == 2) {
            matchAttrs.put(new BasicAttribute("st", sOu1));
            matchAttrs.put(new BasicAttribute("userPassword", Claves.ldapMd5Password(sDni)));
            BasicAttribute oc = new BasicAttribute("objectClass", "top");
            oc.add("inetOrgPerson");
            oc.add("acctSyncAccount");
            oc.add("posixAccount");
            matchAttrs.put(oc);
            this.ctx.createSubcontext("uid=" + sUid + ",ou=" + sOu + ",dc=unmsm,dc=edu,dc=pe", matchAttrs);
        } else if (lugar == 1) {
            matchAttrs.put(new BasicAttribute("st", Facultad(sOu)));
            matchAttrs.put(new BasicAttribute("userPassword", Claves.ldapMd5Password(sDni)));
            BasicAttribute oc = new BasicAttribute("objectClass", "top");
            oc.add("inetOrgPerson");
            oc.add("acctSyncAccount");
            oc.add("posixAccount");
            matchAttrs.put(oc);
            this.ctx.createSubcontext("uid=" + sUid + ",ou=" + sOu1 + ",ou=" + sOu + ",dc=unmsm,dc=edu,dc=pe", matchAttrs);
        }
    }

    public void agregarUsuarioDocente(int lugar, String sEN, String sUid, String sUidNumber, String sGidNumber, String sCodigo, String sSn, String sGivenName, String sEmail, String sDni, String sOu, String sOu1, String sTipo) throws Exception {
        String sNombres = sGivenName.trim() + " " + sSn.trim();
        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute("acctSyncWinSAMAccountName", sUid));
        matchAttrs.put(new BasicAttribute("cn", sNombres.toUpperCase()));
        matchAttrs.put(new BasicAttribute("gidNumber", sGidNumber));
        matchAttrs.put(new BasicAttribute("homeDirectory", "/var/spool/imap"));
        matchAttrs.put(new BasicAttribute("sn", sSn.toUpperCase()));
        matchAttrs.put(new BasicAttribute("uid", sUid));
        matchAttrs.put(new BasicAttribute("uidNumber", sUidNumber));
        matchAttrs.put(new BasicAttribute("acctSyncWinPassword", sCodigo));
        matchAttrs.put(new BasicAttribute("displayName", sNombres.toUpperCase()));
        matchAttrs.put(new BasicAttribute("givenName", sGivenName.toUpperCase()));
        matchAttrs.put(new BasicAttribute("employeeNumber", sEN));
        matchAttrs.put(new BasicAttribute("mail", sEmail));
        matchAttrs.put(new BasicAttribute("o", sTipo));
        if (lugar == 2) {
            matchAttrs.put(new BasicAttribute("st", sOu1.toUpperCase()));
            matchAttrs.put(new BasicAttribute("userPassword", Claves.ldapMd5Password(sDni)));
            BasicAttribute oc = new BasicAttribute("objectClass", "top");
            oc.add("inetOrgPerson");
            oc.add("acctSyncAccount");
            oc.add("posixAccount");
            matchAttrs.put(oc);
            this.ctx.createSubcontext("uid=" + sUid + ",ou=" + sOu + ",dc=unmsm,dc=edu,dc=pe", matchAttrs);
        } else if (lugar == 1) {
            matchAttrs.put(new BasicAttribute("st", Facultad(sOu)));
            matchAttrs.put(new BasicAttribute("userPassword", Claves.ldapMd5Password(sDni)));
            BasicAttribute oc = new BasicAttribute("objectClass", "top");
            oc.add("inetOrgPerson");
            oc.add("acctSyncAccount");
            oc.add("posixAccount");
            matchAttrs.put(oc);
            this.ctx.createSubcontext("uid=" + sUid + ",ou=" + sOu1 + ",ou=" + sOu + ",dc=unmsm,dc=edu,dc=pe", matchAttrs);
        }
    }

    public void agregarUsuarioOficina(int lugar, String sUid, String sUidNumber, String sGidNumber, String sCodigo, String sSn, String sGivenName, String sEmail, String sDni, String sOu, String sOu1, String sTipo) throws Exception {
        String sNombres = sGivenName.trim() + " " + sSn.trim();
        Attributes matchAttrs = new BasicAttributes(true);
        matchAttrs.put(new BasicAttribute("acctSyncWinSAMAccountName", sUid));
        matchAttrs.put(new BasicAttribute("cn", sNombres.toUpperCase()));
        matchAttrs.put(new BasicAttribute("gidNumber", sGidNumber));
        matchAttrs.put(new BasicAttribute("homeDirectory", "/var/spool/imap"));
        matchAttrs.put(new BasicAttribute("sn", sSn.toUpperCase()));
        matchAttrs.put(new BasicAttribute("uid", sUid));
        matchAttrs.put(new BasicAttribute("uidNumber", sUidNumber));
        matchAttrs.put(new BasicAttribute("acctSyncWinPassword", sCodigo));
        matchAttrs.put(new BasicAttribute("displayName", sNombres.toUpperCase()));
        matchAttrs.put(new BasicAttribute("givenName", sGivenName.toUpperCase()));
        matchAttrs.put(new BasicAttribute("mail", sEmail));
        matchAttrs.put(new BasicAttribute("o", sTipo));
        if (lugar == 2) {
            matchAttrs.put(new BasicAttribute("st", sOu1.toUpperCase()));
            matchAttrs.put(new BasicAttribute("userPassword", Claves.ldapMd5Password(sDni)));
            BasicAttribute oc = new BasicAttribute("objectClass", "top");
            oc.add("inetOrgPerson");
            oc.add("acctSyncAccount");
            oc.add("posixAccount");
            matchAttrs.put(oc);
            this.ctx.createSubcontext("uid=" + sUid + ",ou=" + sOu + ",dc=unmsm,dc=edu,dc=pe", matchAttrs);
        } else if (lugar == 1) {
            matchAttrs.put(new BasicAttribute("st", Facultad(sOu)));
            matchAttrs.put(new BasicAttribute("userPassword", Claves.ldapMd5Password(sDni)));
            BasicAttribute oc = new BasicAttribute("objectClass", "top");
            oc.add("inetOrgPerson");
            oc.add("acctSyncAccount");
            oc.add("posixAccount");
            matchAttrs.put(oc);
            this.ctx.createSubcontext("uid=" + sUid + ",ou=" + sOu1 + ",ou=" + sOu + ",dc=unmsm,dc=edu,dc=pe", matchAttrs);
        }
    }

    public int cambioClave(String sUid, String sPassword) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(2);
        String filter = "(uid=" + sUid + ")";
        NamingEnumeration<SearchResult> answer = this.ctx.search("dc=unmsm,dc=edu,dc=pe", filter, ctls);
        int cuenta = 0;
        while (answer.hasMoreElements()) {
            SearchResult sr = answer.next();
            Attributes attributes = sr.getAttributes();
            Attribute pwd = attributes.get("userPassword");
            String password = new String((byte[])pwd.get());
            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod0 = new BasicAttribute("userPassword", Claves.ldapMd5Password(sPassword));
            mods[0] = new ModificationItem(2, mod0);
            this.ctx.modifyAttributes(sr.getName() + ",dc=unmsm,dc=edu,dc=pe", mods);
            cuenta++;
        }
        return cuenta;
    }

    public String verificaClave(String sUid, String sPassword) throws NamingException {
        String r = null;
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(2);
        String filter = "(uid=" + sUid + ")";
        NamingEnumeration<SearchResult> answer = this.ctx.search("dc=unmsm,dc=edu,dc=pe", filter, ctls);
        if (answer.hasMoreElements()) {
            String fullDN = ((SearchResult)answer.next()).getNameInNamespace();
            Properties env = new Properties();
            env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
            env.put("java.naming.provider.url", "ldap://ldap.unmsm.edu.pe:389");
            env.put("java.naming.security.authentication", "simple");
            env.put("java.naming.security.principal", fullDN);
            env.put("java.naming.security.credentials", sPassword);
            try {
                DirContext cx = new InitialDirContext(env);
                cx.close();
                r = "";
            } catch (NamingException ex) {
                r = "La clave es incorrecta";
            }
        } else {
            r = "El usuario no existe";
        }
        return r;
    }

    public int obtenerAtributos(String sUid) throws NamingException {
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(2);
        String filter = "(uid=" + sUid + ")";
        NamingEnumeration<SearchResult> answer = this.ctx.search("dc=unmsm,dc=edu,dc=pe", filter, ctls);
        int cuenta = 0;
        while (answer.hasMoreElements()) {
            SearchResult sr = answer.next();
            Attributes attributes = sr.getAttributes();
            NamingEnumeration<? extends Attribute> attrs = attributes.getAll();
            while (attrs.hasMore())
                System.out.println(attrs.next());
            cuenta++;
        }
        return cuenta;
    }

    public List<String> obtenerGrupos() throws NamingException {
        SearchControls ctls = new SearchControls();
        ctls.setReturningAttributes(new String[] { "ou" });
        ctls.setSearchScope(1);
        String filter = "(objectclass=organizationalUnit)";
        NamingEnumeration<SearchResult> answer = this.ctx.search("dc=unmsm,dc=edu,dc=pe", filter, ctls);
        List<String> asGrupo = new ArrayList<>();
        String sGrupo = " ";
        while (answer.hasMoreElements()) {
            SearchResult sr = answer.next();
            Attributes attributes = sr.getAttributes();
            Attribute oGrupo = attributes.get("ou");
            sGrupo = (String)oGrupo.get();
            asGrupo.add(sGrupo);
        }
        return asGrupo;
    }

    public boolean verificaGrupo(String sUid, String sGid) throws NamingException {
        String[] returnedAttrs = { "memberUid" };
        SearchControls ctls = new SearchControls();
        ctls.setReturningAttributes(returnedAttrs);
        ctls.setSearchScope(2);
        String filter = "(&(objectClass=posixGroup)(cn=" + sGid + "))";
        NamingEnumeration<SearchResult> answer = this.ctx.search("dc=unmsm,dc=edu,dc=pe", filter, ctls);
        int cuenta = 0;
        boolean encontro = false;
        while (answer.hasMoreElements()) {
            SearchResult sr = answer.next();
            Attributes attributes = sr.getAttributes();
            NamingEnumeration<? extends Attribute> attrs = attributes.getAll();
            while (attrs.hasMore()) {
                Attribute attr = attrs.next();
                NamingEnumeration<?> valores = attr.getAll();
                while (valores.hasMore()) {
                    String valor = valores.next().toString();
                    if (valor.equalsIgnoreCase(sUid))
                        encontro = true;
                }
            }
            cuenta++;
        }
        return encontro;
    }

    public String mayuscula(String cadena) {
        String cadena1 = " ";
        int i = 0;
        while (cadena.charAt(i) != ' ') {
            cadena1 = cadena1 + cadena.charAt(i);
            i++;
        }
        return cadena1.trim().toUpperCase();
    }

    public String Facultad(String cadena) {
        String cadena1 = " ";
        for (int i = 12; i < cadena.length(); i++)
            cadena1 = cadena1 + cadena.charAt(i);
        return cadena1.trim().toUpperCase();
    }

    public int Buscar(String sUid) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(2);
        String filter = "(uid=" + sUid + ")";
        NamingEnumeration<SearchResult> answer = this.ctx.search("dc=unmsm,dc=edu,dc=pe", filter, ctls);
        int cuenta = 0;
        while (answer.hasMoreElements()) {
            SearchResult sr = answer.next();
            cuenta++;
        }
        return cuenta;
    }

    public List<String> obtenerGrupos1(int i, String tipo) throws NamingException {
        List<String> tipo1 = new ArrayList<>();
        if (i != 0)
            if (tipo.equalsIgnoreCase("Alumno")) {
                tipo1.add(0, "Pregrado " + (String)this.facultades.get(i));
                tipo1.add(1, "Posgrado " + (String)this.facultades.get(i));
                tipo1.add(2, "Biblioteca " + (String)this.facultades.get(i));
            } else if (tipo.equalsIgnoreCase("Administrativo")) {
                tipo1.add(0, tipo + " " + (String)this.facultades.get(i));
            } else if (tipo.equalsIgnoreCase("Docente")) {
                tipo1.add(0, tipo + " " + (String)this.facultades.get(i));
            } else if (tipo.equalsIgnoreCase("Oficina")) {
                tipo1.add(0, tipo + " " + (String)this.facultades.get(i));
            }
        return tipo1;
    }
}
