package org.microprofileext.openapi.swaggerui;

import java.io.*;
import java.util.logging.Level;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
        StreamingOutput result = new StreamingOutput() {
            @Override
            public void write(OutputStream output)
                    throws WebApplicationException {
                try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(String.format("META-INF/resources/%s", path))) {
                    byte[] buffer = new byte[1024];
                    int len = inputStream.read(buffer);
                    while (len != -1) {
                        output.write(buffer, 0, len);
                        len = inputStream.read(buffer);
                    }
                } catch (IOException ex) {
                    log.severe(ex.getMessage());
                    throw new InternalServerErrorException(ex);
                } catch (NullPointerException ex) {
                    log.log(Level.WARNING, "Could not find resource [{0}]", path);
                    throw new NotFoundException();
                }
            }
        };
        Response.ResponseBuilder ret = Response.ok(result);
        if (path.endsWith(".js")) {
            ret = ret.type("application/javascript");
        }
        return ret.build();
    }
    
}