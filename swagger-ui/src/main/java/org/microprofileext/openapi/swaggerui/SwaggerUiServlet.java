package org.microprofileext.openapi.swaggerui;

import lombok.extern.java.Log;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Objects;
import java.util.logging.Level;

@Log
@WebServlet({
        "/webjars/swagger-ui/*",
        "/webjars/swagger-ui-themes/*"})
public class SwaggerUiServlet extends HttpServlet {

    private final String CONTENT_TYPE_CSS = "text/css";
    private final String CONTENT_TYPE_HTML = "text/html";
    private final String CONTENT_TYPE_JS = "text/plain";
    private final String CONTENT_TYPE_PNG = "image/png";


    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = getPath(req.getRequestURI());
        if(!validateRequest(path)){
            resp.sendError(404, "Not Found");
            return;
        }

        try(InputStream input = this.getClass().getClassLoader().getResourceAsStream(path)) {
            if(Objects.isNull(input)){
                resp.sendError(404, "Not Found");
                return;
            }

            resp.setContentType(getContentType(path));
            if(isPngRequest(path)){
                writeAsFile(resp,input);
            }
            else{
                writeAsString(resp,input);
            }

        }

    }

    private void writeAsString(HttpServletResponse resp,InputStream inputStream) throws IOException {
        PrintWriter printWriter = resp.getWriter();
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
            buffer.lines().forEach(printWriter::println);
        }
    }

    private void writeAsFile(HttpServletResponse resp, InputStream inputStream){
        try{
            OutputStream out = resp.getOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buf)) >= 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
        }


    }

    private String getPath(String uri){
        return String.format("%s/%s", "META-INF/resources/",uri);
    }

    private String getContentType(String path){
        String ext = path.substring(path.lastIndexOf('.')+1);

        if("CSS".equals(ext.toUpperCase())){
            return CONTENT_TYPE_CSS;
        }

        if("JS".equals(ext.toUpperCase())){
            return CONTENT_TYPE_JS;
        }

        if("HTML".equals(ext.toUpperCase())){
            return CONTENT_TYPE_HTML;
        }

        if("PNG".equals(ext.toUpperCase())){
            return CONTENT_TYPE_PNG;
        }

        return null;
    }

    private boolean isCssRequest(String path){
        return CONTENT_TYPE_CSS.equals(getContentType(path));
    }

    private boolean isPngRequest(String path){
        return CONTENT_TYPE_PNG.equals(getContentType(path));
    }

    private boolean isHtmlRequest(String path){
        return CONTENT_TYPE_HTML.equals(getContentType(path));
    }

    private boolean isJsRequest(String path){
        return CONTENT_TYPE_JS.equals(getContentType(path));
    }

    private boolean validateRequest(String path){
        if(isPngRequest(path) || isCssRequest(path)
                || isHtmlRequest(path) || isJsRequest(path)){
            return true;
        }
        return false;
    }



}
