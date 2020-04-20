package org.microprofileext.openapi.swaggerui;

import java.net.URI;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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