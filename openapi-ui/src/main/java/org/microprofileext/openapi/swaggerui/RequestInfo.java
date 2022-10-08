package org.microprofileext.openapi.swaggerui;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Holding info about the request
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@AllArgsConstructor @NoArgsConstructor
public class RequestInfo {
    @Getter
    private UriInfo uriInfo;
    @Getter
    private HttpHeaders httpHeaders;
    
    public String getContextPath(){
        if(!isValid())return EMPTY;   
        String path = this.uriInfo.getBaseUri().getPath();
        return getContextPath(path);
    }
    
    public String getContextPath(String path){
        if(path.startsWith(SLASH))path = path.substring(1);
        if(path.endsWith(SLASH))path = path.substring(0, path.length());
        if(path.contains(SLASH)){
            return SLASH + path.substring(0,path.indexOf(SLASH));
        } else {
            return EMPTY; // Asume no context path ?
        }
    }
    
    public String getRestPath(){
        if(!isValid())return EMPTY;
        String path = this.uriInfo.getBaseUri().getPath();
        return getRestPath(path);
    }
    
    public String getRestPath(String path){
        if(path.startsWith(SLASH))path = path.substring(1);
        if(path.endsWith(SLASH))path = path.substring(0, path.length()-1);
        if(path.contains(SLASH)){
            return path.substring(path.indexOf(SLASH));
        } else {
            return SLASH + path;
        }
    }
    
    private boolean isValid(){
        if(this.uriInfo==null)return false;
        
        URI baseUri = this.uriInfo.getBaseUri();
        if(baseUri==null)return false;
        
        String path = baseUri.getPath();
        if(path==null || path.isEmpty()) return false;
        
        return true;
    }
    
    private static final String EMPTY = "";
    private static final String SLASH = "/";
}
