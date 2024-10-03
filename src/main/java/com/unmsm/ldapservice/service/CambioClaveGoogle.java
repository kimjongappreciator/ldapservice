package com.unmsm.ldapservice.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserName;
import com.google.api.services.oauth2.Oauth2;
import com.unmsm.ldapservice.helper.Claves;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;


public class CambioClaveGoogle {
    private static final Logger log = LoggerFactory.getLogger(CambioClaveGoogle.class);
    private final String APPLICATION_NAME = "cambioclave20151/1.0";

    private final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/CambioClave20151");

    private FileDataStoreFactory dataStoreFactory = null;

    private HttpTransport httpTransport = null;

    private final JsonFactory JSON_FACTORY = (JsonFactory)GsonFactory.getDefaultInstance();

    private final List<String> SCOPES = Arrays.asList(new String[] { "https://www.googleapis.com/auth/admin.directory.user", "https://www.googleapis.com/auth/admin.directory.group" });

    private Oauth2 oauth2 = null;

    private GoogleClientSecrets clientSecrets;

    private Credential credential = null;

    private Directory service = null;

    public User agregarUsuario(String sEmail, String sClave) throws IOException, Throwable {
        User user = new User();
        UserName name = new UserName();
        name.setFamilyName("Prueba 01");
        name.setGivenName("Telematica");
        user.setName(name);
        user.setHashFunction("MD5");
        user.setPassword(Claves.googleMd5Password(sClave));
        user.setPrimaryEmail(sEmail);
        user.setOrgUnitPath("/Users");
        user = (User)this.service.users().insert(user).execute();
        return user;
    }

    public User agregarUsuario(String sApellido, String sNombre, String sEmail, String sClave, String sGrupo) throws IOException, Throwable {
        User user = new User();
        UserName name = new UserName();
        name.setFamilyName(sApellido.toUpperCase());
        name.setGivenName(sNombre.toUpperCase());
        user.setName(name);
        user.setHashFunction("MD5");
        user.setPassword(Claves.googleMd5Password(sClave));
        user.setPrimaryEmail(sEmail);
        user.setOrgUnitPath("/" + sGrupo);
        user = (User)this.service.users().insert(user).execute();
        return user;
    }

    public CambioClaveGoogle() throws IOException, GeneralSecurityException, Exception {
        this.httpTransport = (HttpTransport)GoogleNetHttpTransport.newTrustedTransport();
        //System.out.println(this.DATA_STORE_DIR.getAbsolutePath());
        this.dataStoreFactory = new FileDataStoreFactory(this.DATA_STORE_DIR);
        this.credential = authorize();
        this.oauth2 = (new Oauth2.Builder(this.httpTransport, this.JSON_FACTORY, (HttpRequestInitializer)this.credential)).setApplicationName("cambioclave20151/1.0").build();
        this.service = (new Directory.Builder(this.httpTransport, this.JSON_FACTORY, (HttpRequestInitializer)this.credential)).setApplicationName("cambioclave20151/1.0").build();
    }

    public String obtenerUsuario(String sEmail) throws IOException {
        User user;
        try {
            user = (User)this.service.users().get(sEmail).execute();
        } catch (GoogleJsonResponseException ex) {
            return null;
        }
        return user.toPrettyString();
    }

    public String obtenerGrupos(String sEmail) throws IOException {
        String sGrupos = "";
        Groups groups = (Groups)this.service.groups().list().setUserKey(sEmail).execute();
        List<Group> list = groups.getGroups();
        for (int i = 0; i < list.size(); i++) {
            Group g = list.get(i);
            if (i > 0)
                sGrupos = sGrupos + ",";
            sGrupos = sGrupos + g.getName();
        }
        return sGrupos;
    }

    public int cambioClave(String sEmail, String sPassword) throws IOException, Throwable {
        Directory.Users.Get req = this.service.users().get(sEmail);
        User user = (User)req.execute();
        user.setHashFunction("MD5");
        user.setPassword(Claves.googleMd5Password(sPassword));
        Directory.Users.Update upd = this.service.users().update(user.getPrimaryEmail(), user);
        upd.execute();
        return 1;
    }

    private Credential authorize() throws IOException {
        String creds_file_path = System.getProperty("user.dir")+ "\\clientsecrets.json"; //windows
        //String creds_file_path = System.getProperty("user.dir")+ "/clientsecrets.json"; //linux
        System.out.println(creds_file_path);
        InputStream in = new FileInputStream(creds_file_path);
        if (in == null) {
            log.info("No credential found @ {}", creds_file_path);
        }
        this.clientSecrets = GoogleClientSecrets.load(this.JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = (new GoogleAuthorizationCodeFlow.Builder(this.httpTransport, this.JSON_FACTORY, this.clientSecrets, this.SCOPES)).setDataStoreFactory((DataStoreFactory)this.dataStoreFactory).build();
        return (new AuthorizationCodeInstalledApp((AuthorizationCodeFlow)flow, (VerificationCodeReceiver)new LocalServerReceiver())).authorize("appservidores@unmsm.edu.pe");
    }
}
