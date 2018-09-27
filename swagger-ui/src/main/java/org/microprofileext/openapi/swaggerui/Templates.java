package org.microprofileext.openapi.swaggerui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;
import lombok.Getter;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.Config;

/**
 * Helping with the templates (
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@ApplicationScoped
public class Templates {  
 
    @Getter
    private byte[] originalLogo = null;
    @Getter
    private byte[] favicon32 = null;
    @Getter
    private byte[] favicon16 = null;
    @Getter
    private String style = null;
    
    @Inject
    private WhiteLabel whiteLabel;
    
    private String swaggerUIHtml = null;
    
    @PostConstruct
    public void afterCreate() {
        BufferedImage image = getLogo();
        BufferedImage image16 = getFavicon(16, image);
        BufferedImage image32 = getFavicon(32, image);
        try {
            this.originalLogo = toBytes(image);
            log.finest("OpenApi UI: Created logo");
            this.favicon16 = toBytes(image16);
            this.favicon32 = toBytes(image32);
            log.finest("OpenApi UI: Created favicons");
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        this.style = getCss();
        
    }
    
    public String getSwaggerUIHtml(UriInfo uriInfo,HttpServletRequest request){
        if(this.swaggerUIHtml==null){
            this.swaggerUIHtml = parseHtmlTemplate(uriInfo,request);
        }
        return this.swaggerUIHtml;
    }
    
    public byte[] getFavicon(int size){
        if(size>24)return getFavicon32();
        return getFavicon16();
    }
    
    private String parseHtmlTemplate(UriInfo uriInfo, HttpServletRequest request){
        String html = getHTMLTemplate();
        
        html = html.replaceAll(VAR_CONTEXT_ROOT, getContextRoot(uriInfo,request));
        html = html.replaceAll(VAR_YAML_URL, yamlUrl);
        html = html.replaceAll(VAR_CURRENT_YEAR, getCopyrightYear());
        
        // Dynamic whitelabel properties.
        try {
            Iterable<String> propertyNames = config.getPropertyNames();
            for(String key: propertyNames){
                if(key.startsWith(KEY_IDENTIFIER) && !isKnownProperty(key)){
                    String htmlKey = PERSENTAGE + key + PERSENTAGE;
                    html = html.replaceAll(htmlKey, config.getValue(key,String.class));
                }
            }
        }catch(UnsupportedOperationException uoe){
            log.log(Level.WARNING, "Can not replace dynamic properties in the Open API Swagger template. {0}", uoe.getMessage());
        }
        // Then properties with defaults.
        html = html.replaceAll(VAR_COPYRIGHT_BY, copyrightBy);
        html = html.replaceAll(VAR_TITLE, title);
        html = html.replaceAll(VAR_SWAGGER_THEME, swaggerUiTheme);
        html = html.replaceAll(VAR_SERVER_INFO, getServerInfo(request));
        
        return html;
    }
    
    private static final String X_REQUEST_URI = "x-request-uri";
    private String getOriginalContextPath(UriInfo uriInfo,HttpServletRequest request){
        String fromHeader = request.getHeader(X_REQUEST_URI);
        
        if(fromHeader!=null && !fromHeader.isEmpty()){
            return getContextPathPart(uriInfo,request,fromHeader);
        }
        return request.getContextPath();
    }
    
    private String getContextPathPart(UriInfo uriInfo,HttpServletRequest request, String fromHeader){
        
        String restBase = request.getServletPath();
        String restUrl = restBase + uriInfo.getPath();
        
        int restUrlStart = fromHeader.indexOf(restUrl);
        
        if(restUrlStart>0){
            return fromHeader.substring(0, restUrlStart);
        }else{
            return fromHeader;
        }
        
    }
    
    private String toString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining(NL));
        }
    }
    
    private BufferedImage getLogo(){
        if(whiteLabel.hasLogo())return whiteLabel.getLogo();
        
        try(InputStream logo = this.getClass().getClassLoader().getResourceAsStream(FILE_LOGO)){
            return ImageIO.read(logo);    
        } catch (IOException ex) {
            return null;
        }
    }
    
    private String getCss() {
        String rawcss;
        
        if(whiteLabel.hasCss()){
            rawcss = whiteLabel.getCss();
        }else {
        
            try(InputStream css = this.getClass().getClassLoader().getResourceAsStream(FILE_STYLE)){
                rawcss = toString(css);    
            } catch (IOException ex) {
                return EMPTY;
            }
        }
        
        rawcss = rawcss.replaceAll(VAR_SWAGGER_HEADER_VISIBILITY, swaggerHeaderVisibility);
        rawcss = rawcss.replaceAll(VAR_EXPLORE_FORM_VISIBILITY, exploreFormVisibility);
        rawcss = rawcss.replaceAll(VAR_SERVER_VISIBILITY, serverVisibility);
        rawcss = rawcss.replaceAll(VAR_SERVER_VISIBILITY_BLOCK_SIZE, getServerVisibilityBlockSize());
        rawcss = rawcss.replaceAll(VAR_CREATED_WITH_VISIBILITY, createdWithVisibility);
        
        return rawcss;
    }
    
    private String getServerVisibilityBlockSize() {
        if(serverVisibility.equals("hidden"))return "0px";
        return "auto";
    }
    
    private String getHTMLTemplate() {
        if(whiteLabel.hasHtml())return whiteLabel.getHtml();
        try(InputStream template = this.getClass().getClassLoader().getResourceAsStream(FILE_TEMPLATE)){
            return toString(template);    
        } catch (IOException ex) {
            return EMPTY;
        }
    }
    
    private BufferedImage getFavicon(int size,BufferedImage original){
        int type = original.getType() == 0? BufferedImage.TYPE_INT_ARGB : original.getType();
        return resizeImage(size,original, type);
    }
    
    private BufferedImage resizeImage(int size,BufferedImage originalImage, int type){
	BufferedImage resizedImage = new BufferedImage(size, size, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(originalImage, 0, 0, size, size, null);
	g.dispose();

	return resizedImage;
    }
    
    private byte[] toBytes(BufferedImage bufferedImage) throws IOException{
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(bufferedImage, PNG, baos);
            return baos.toByteArray();
        }
    }
    
    private String getCopyrightYear(){
        if(copyrightYear==null || copyrightYear.isEmpty()){
            return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        }
        return copyrightYear;
    }
    
    private String getServerInfo(HttpServletRequest request){
        if(serverInfo==null || serverInfo.isEmpty()){
            return request.getServletContext().getServerInfo();
        }
        return serverInfo;
    }
    
    private String getContextRoot(UriInfo uriInfo,HttpServletRequest request){
        if(contextRoot==null || contextRoot.isEmpty()){
            return getOriginalContextPath(uriInfo,request);
        }
        return contextRoot;
    }
    
    private boolean isKnownProperty(String key){
        return KNOWN_PROPERTIES.contains(key);
    }
    
    private static final List<String> KNOWN_PROPERTIES = Arrays.asList(new String[]{"openapi-ui.serverVisibility","openapi-ui.exploreFormVisibility","openapi-ui.swaggerHeaderVisibility","openapi-ui.copyrightBy","openapi-ui.copyrightYear","openapi-ui.title","openapi-ui.serverInfo","openapi-ui.contextRoot","openapi-ui.yamlUrl","openapi-ui.swaggerUiTheme"});
    
    @Inject @ConfigProperty(name = "openapi-ui.copyrightBy", defaultValue = "")
    private String copyrightBy;
    
    @Inject @ConfigProperty(name = "openapi-ui.copyrightYear", defaultValue = "")
    private String copyrightYear;
    
    @Inject @ConfigProperty(name = "openapi-ui.title", defaultValue = "MicroProfile - Open API")
    private String title;
    
    @Inject @ConfigProperty(name = "openapi-ui.serverInfo", defaultValue = "")
    private String serverInfo;
    
    @Inject @ConfigProperty(name = "openapi-ui.contextRoot", defaultValue = "")
    private String contextRoot;
    
    @Inject @ConfigProperty(name = "openapi-ui.yamlUrl", defaultValue = "/openapi")
    private String yamlUrl;
    
    @Inject @ConfigProperty(name = "openapi-ui.swaggerUiTheme", defaultValue = "flattop")
    private String swaggerUiTheme;
    
    @Inject @ConfigProperty(name = "openapi-ui.swaggerHeaderVisibility", defaultValue = "visible")
    private String swaggerHeaderVisibility;
    
    @Inject @ConfigProperty(name = "openapi-ui.exploreFormVisibility", defaultValue = "hidden")
    private String exploreFormVisibility;
    
    @Inject @ConfigProperty(name = "openapi-ui.serverVisibility", defaultValue = "hidden")
    private String serverVisibility;
    
    @Inject @ConfigProperty(name = "openapi-ui.createdWithVisibility", defaultValue = "visible")
    private String createdWithVisibility;
    
    @Inject 
    private Config config;

    private static final String VAR_COPYRIGHT_BY = "%copyrighBy%";
    private static final String VAR_TITLE = "%title%";
    private static final String VAR_CURRENT_YEAR = "%currentYear%";
    private static final String VAR_SERVER_INFO = "%serverInfo%";
    private static final String VAR_CONTEXT_ROOT = "%contextRoot%";
    private static final String VAR_YAML_URL = "%yamlUrl%";
    
    private static final String VAR_SWAGGER_THEME = "%swaggerUiTheme%";
    private static final String VAR_SWAGGER_HEADER_VISIBILITY = "%swaggerHeaderVisibility%";
    private static final String VAR_EXPLORE_FORM_VISIBILITY = "%exploreFormVisibility%";
    private static final String VAR_SERVER_VISIBILITY = "%serverVisibility%";
    private static final String VAR_SERVER_VISIBILITY_BLOCK_SIZE = "%serverVisibilityBlockSize%";
    private static final String VAR_CREATED_WITH_VISIBILITY = "%createdWithVisibility%";
    
    private static final String PERSENTAGE = "%";
    
    private static final String PNG = "png";
    private static final String NL = "\n";
    private static final String EMPTY = "";
    private static final String FILE_TEMPLATE = "META-INF/resources/templates/template.html";
    private static final String FILE_LOGO = "META-INF/resources/templates/logo.png";
    private static final String FILE_STYLE = "META-INF/resources/templates/style.css";
    private static final String KEY_IDENTIFIER = "openapi-ui.";

}