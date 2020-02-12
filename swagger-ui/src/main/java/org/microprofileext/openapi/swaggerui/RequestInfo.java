package org.microprofileext.openapi.swaggerui;

import java.net.URI;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Holding info about the request
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
@AllArgsConstructor
public class RequestInfo {
    @Getter
    private UriInfo uriInfo;
    @Getter
    private HttpHeaders httpHeaders;
    
    public String getContextPath(){
        if(!isValid())return EMPTY;
        
        String path = this.uriInfo.getBaseUri().getPath();
        if(path.startsWith(SLASH))path = path.substring(1);
        if(path.endsWith(SLASH))path = path.substring(0, path.length());
        if(path.contains(SLASH)){
            return SLASH + path.substring(0,path.indexOf(SLASH));
        } else {
            return SLASH + path;
        }
    }
    
    public String getRestPath(){
        if(!isValid())return EMPTY;
        
        String path = this.uriInfo.getBaseUri().getPath();
        if(path.startsWith(SLASH))path = path.substring(1);
        if(path.endsWith(SLASH))path = path.substring(0, path.length()-1);
        if(path.contains(SLASH)){
            return path.substring(path.indexOf(SLASH));
        } else {
            return SLASH;
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
