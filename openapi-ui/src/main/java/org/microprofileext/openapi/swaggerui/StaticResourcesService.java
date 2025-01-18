package org.microprofileext.openapi.swaggerui;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * Serving static data from webjars
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@Log
@Path("{path: webjars/.*}")
public class StaticResourcesService {

    @GET
    @Operation(hidden = true)
    public Response staticJsResources(@PathParam("path") final String path) {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(String.format("META-INF/resources/%s", path))) {
            if(Objects.isNull(inputStream)) {
                log.log(Level.WARNING, "Could not find resource [{0}]", path);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            try ( BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringWriter stringWriter = new StringWriter()) {
                buffer.lines().forEach(stringWriter::write);
                String response = stringWriter.toString();
                if (response != null && !response.isEmpty()) {
                    Response.ResponseBuilder ret = Response.ok(response);
                    if (path.endsWith(".js")) {
                        ret = ret.type("application/javascript");
                    }
                    return ret.build();
                } else {
                    log.log(Level.WARNING, "The requested swagger file is empty!");
                    return Response.noContent().build();
                }
            }
        } catch (IOException | NullPointerException ex) {
            log.log(Level.SEVERE, "Failed to read the static Swagger file at path: {0}. Error details: {1}", new Object[]{path, ex.getMessage()});
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}