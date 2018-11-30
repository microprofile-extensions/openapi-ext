package org.microprofileext.openapi.example;

import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Person Service. JAX-RS
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
@RequestScoped
@Path("/")
@Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Person service",description = "Just some example")
public class PersonService {
    
    @Inject
    private Map<Integer,Person> people;
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "all", description = "Getting all people")
    @APIResponse(responseCode = "200", description = "Successful, returning the map in JSON format")
    public Response getAll(){
        
        return Response.ok(people).build();
    }
    
    @GET
    @Path("/id/{id}")
    @Operation(operationId = "value", description = "Getting the person by id")
    @APIResponse(responseCode = "200", description = "Successful, returning the person")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerson(@Parameter(name = "id", description = "The id for the person", required = true, allowEmptyValue = false, example = "1")
                                @PathParam("id") Integer id) {
        
        if(people.containsKey(id)){
            Person p = people.get(id);
            return Response.ok(p).build();
        }else{
            return Response.noContent().build();   
        }
        
    }
    
}
