package org.microprofileext.openapi.swaggerui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;

/**
 * Serving static data from webjars
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@Log
@Path("{path: webjars/.*}")
public class StaticResourcesService {

    @GET
    public Response staticJsResources(@PathParam("path") final String path) {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(String.format("META-INF/resources/%s", path));
                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
                StringWriter stringWriter = new StringWriter()) {
            buffer.lines().forEach(stringWriter::write);

            String response = stringWriter.toString();
            if (response != null && !response.isEmpty()) {
                Response.ResponseBuilder ret = Response.ok(response);
                if (path.endsWith(".js")) {
                    ret = ret.type("application/javascript");
                }
                return ret.build();
            }
        } catch (IOException ex) {
            log.severe(ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        log.log(Level.WARNING, "Could not find resource [{0}]", path);
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
}