package org.microprofileext.openapi.swaggerui;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * White label the UI
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@ApplicationScoped
public class WhiteLabel {
    
    private final String LOGO_FILE_NAME = "openapi.png";
    private final String CSS_FILE_NAME = "openapi.css";
    private final String HTML_FILE_NAME = "openapi.html";
    
    @Getter
    private BufferedImage logo = null;
    
    @Getter
    private String css = null;
    @Getter
    private String html = null;
    
    @PostConstruct
    public void init(){
        
        // Logo
        try(InputStream logoStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(LOGO_FILE_NAME)){
            if(logoStream!=null){
                this.logo = ImageIO.read(logoStream);
            }else{
                log.log(Level.FINEST, "Can not load whilelable logo [{0}]", LOGO_FILE_NAME);
            }
        } catch (IOException ex) {
            log.log(Level.FINEST, "Can not load whilelable logo [{0}] - {1}", new Object[]{LOGO_FILE_NAME,ex.getMessage()});
        }
        
        // Css
        try(InputStream cssStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CSS_FILE_NAME)){
            if(cssStream!=null){
                this.css = toString(cssStream);
            }else{
                log.log(Level.FINEST, "Can not load whilelable css [{0}]", CSS_FILE_NAME);
            }
        } catch (IOException ex) {
            log.log(Level.FINEST, "Can not load whilelable css [{0}] - {1}", new Object[]{CSS_FILE_NAME, ex.getMessage()});
        }
        
        // Html
        try(InputStream htmlStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(HTML_FILE_NAME)){
            if(htmlStream!=null){
                this.html = toString(htmlStream);
            }else{
                log.log(Level.FINEST, "Can not load whilelable html [{0}]", HTML_FILE_NAME);
            }
        } catch (IOException ex) {
            log.log(Level.FINEST, "Can not load whilelable html [{0}] - {1}", new Object[]{HTML_FILE_NAME, ex.getMessage()});
        }
    }
    
    public boolean hasLogo(){
        return logo!=null;
    }
    
    public boolean hasCss() {
        return css!=null;
    }
    
    public boolean hasHtml() {
        return html!=null;
    }
    
    private String toString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining(NL));
        }
    }
    private static final String NL = "\n";
}
