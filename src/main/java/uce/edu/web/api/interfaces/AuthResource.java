package uce.edu.web.api.interfaces;

import java.time.Instant;
import java.util.Set;

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import uce.edu.web.api.domain.Usuario; 

@Path("auth") 
public class AuthResource {
    
    @GET
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResponse token(
            @QueryParam("user") String user,
            @QueryParam("password") String password) {

        
        Usuario encontrado = Usuario.find("username = ?1 and password = ?2", user, password).firstResult();

       
        if (encontrado == null) {
            throw new WebApplicationException("Usuario o contraseña incorrectos", Response.Status.UNAUTHORIZED);
        }

        
        String role = encontrado.rol; 
        String issuer = "matricula-auth";
        long ttl = 3600;
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttl);
 
        String jwt = Jwt.issuer(issuer)
                .subject(user)
                .groups(Set.of(role))     
                .issuedAt(now)
                .expiresAt(exp)
                .sign();
 
        return new TokenResponse(jwt, exp.getEpochSecond(), role);
    }

    public static class TokenResponse {
         public String accessToken;
         public long expiresAt;
         public String role;
 
         public TokenResponse() {}
         public TokenResponse(String accessToken, long expiresAt, String role) {
             this.accessToken = accessToken;
             this.expiresAt = expiresAt;
             this.role = role;
         }
    }
}