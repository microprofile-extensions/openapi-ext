package org.microprofileext.openapi.swaggerui;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import lombok.extern.java.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * This service creates a HTML document for Swagger UI
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@Path("/openapi-ui/")
@PermitAll
public class OpenApiUiService {  
    
    @Inject
    private Templates templates;
    
    @Context
    private UriInfo uriInfo;
    
    @Context 
    private HttpHeaders httpHeaders;
    
    @GET
    @Produces("image/png")
    @Path("logo.png")
    @Operation(hidden = true)
    public byte[] getLogo(){
        return templates.getOriginalLogo();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path(INDEX_HTML)
    @Operation(hidden = true)
    public Response getOpenApiUI(){
        RequestInfo requestInfo = new RequestInfo(uriInfo, httpHeaders);
        String swaggerUI = templates.getSwaggerUIHtml(requestInfo);
        return Response.ok(swaggerUI, MediaType.TEXT_HTML).build();
    }
    
    @GET
    @Produces("text/css")
    @Path("style.css")
    @Operation(hidden = true)
    public Response getCss(){
        String css = templates.getStyle();
        return Response.ok(css, "text/css").build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public Response getSwaggerUINaked(){
        URI fw = uriInfo.getRequestUriBuilder().path(INDEX_HTML).build();
        return Response.temporaryRedirect(fw).build();
    }
    
    private static final String INDEX_HTML = "index.html";
    
}