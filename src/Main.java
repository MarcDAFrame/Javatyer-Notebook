
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import java.io.*;
import java.util.*;
// import java.util.Scanner;

import java.nio.file.Files;

import java.net.URI;

import com.sun.net.httpserver.*;
// import com.sun.net.httpserver.HttpExchange;
// import com.sun.net.httpserver.HttpHandler;
// import com.sun.net.httpserver.HttpServer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//LOCAL
import utils.RuntimeCompiler;
import utils.MethodInvocationUtils;



public class Main {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/ide", new IDE());
        server.createContext("/run", new Run());
        server.createContext("/save", new Save());
        server.createContext("/load", new Load());
        server.createContext("/json", new JSON());
        server.createContext("/static", new StaticFiles());
        server.setExecutor(null); // creates a default executor
        System.out.println("Server is running on local host with port 8000");
        server.start();    // public static void main(String[] args) throws Exception

    }

    static class JSON implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
                // String response = "{\"test\":\"123\"}";
                
                JSONObject obj = new JSONObject();

                obj.put("name", "foo");
                obj.put("num", new Integer(100));
                obj.put("balance", new Double(1000.21));
                obj.put("is_vip", new Boolean(true));

                StringWriter sw = new StringWriter();
                obj.writeJSONString(sw);
                
                String response = sw.toString();
                // String response = "json";
                t.sendResponseHeaders(200, response.length());
                OutputStream out = t.getResponseBody();
                out.write(response.getBytes());
                out.close();
        }
    }

    static class Run implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            if (t.getRequestMethod().equalsIgnoreCase("POST")) {
                // System.out.println(t.getRequestBody());

                StringBuilder buf=new StringBuilder();

                int b;
                InputStream is=t.getRequestBody();
                while ((b=is.read()) != -1) {
                    buf.append((char)b);
                }
                is.close();
                String request=buf.toString();


                String classNameA = "Run";

                utils.RuntimeCompiler r = new utils.RuntimeCompiler();
                r.addClass(classNameA, request);
                String debug = r.compile();
                if (debug != ""){

                    JSONObject obj = new JSONObject();

                    obj.put("debug", debug);

                    StringWriter sw = new StringWriter();
                    obj.writeJSONString(sw);
                    
                    String response = sw.toString();

                    
                    System.out.println("[202] - response: " + response);
                    
                    t.sendResponseHeaders(200, response.length());
                    OutputStream out = t.getResponseBody();
                    out.write(response.getBytes());
                    out.close();
                }else{
                    ByteArrayOutputStream consolebaos = new ByteArrayOutputStream();
                    PrintStream consoleps = new PrintStream(consolebaos);
                    PrintStream consoleold = System.out;
                    try{

                        System.setOut(consoleps);
                        
                        String output = utils.MethodInvocationUtils.invokeStaticMethod(
                        r.getCompiledClass(classNameA), 
                        "Run");

                        System.out.flush();
                        System.setOut(consoleold);
                        String console = consolebaos.toString();


                        JSONObject obj = new JSONObject();

                        obj.put("output", output);
                        obj.put("console", console);

                        StringWriter sw = new StringWriter();
                        obj.writeJSONString(sw);
                        
                        String response = sw.toString();

                            
                        System.out.println("[200] - response: " + response);
                        
                        t.sendResponseHeaders(200, response.length());
                        OutputStream out = t.getResponseBody();
                        out.write(response.getBytes());
                        out.close();

                    }catch(Exception e){
                        System.setOut(consoleold);
                        // System.out.println(e);
                        e.printStackTrace(new PrintStream(System.out));

                        System.out.println(e);
                        System.out.println(e.getMessage());

                        System.out.println("[202] - error: " + e.getMessage());
                        
                        t.sendResponseHeaders(200, e.getMessage().length());
                        OutputStream out = t.getResponseBody();
                        out.write(e.getMessage().getBytes());
                        out.close();

                    }




                }

            }
        }
    }


    static class Save implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            if (t.getRequestMethod().equalsIgnoreCase("POST")) {

                StringBuilder buf=new StringBuilder();

                int b;
                InputStream is=t.getRequestBody();
                
                while ((b=is.read()) != -1) {
                    buf.append((char)b);
                }
                is.close();
                String request=buf.toString();
                System.out.println(request);

                /* STRING TO JSON */
                JSONObject obj = null;
                try{
                    JSONParser parser = new JSONParser();
                    obj = (JSONObject) parser.parse(request);
                    // System.out.println(obj.get("cell0"));
                }catch(Exception e){
                    System.out.println(e);
                }
                System.out.println("test");
                String directory = obj.get("directory").toString();
                String filename = obj.get("filename").toString();
                System.out.println(directory);
                try (FileWriter file = new FileWriter(directory + filename)) {

                    file.write(obj.toJSONString());
                    file.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                } 
 
                


            }

        }
    }
    static class Load implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            if (t.getRequestMethod().equalsIgnoreCase("POST")) {

                StringBuilder buf=new StringBuilder();

                int b;
                InputStream is=t.getRequestBody();
                
                while ((b=is.read()) != -1) {
                    buf.append((char)b);
                }
                is.close();
                String request=buf.toString();
                System.out.println(request);

                /* STRING TO JSON */
                JSONObject obj = null;
                JSONObject return_obj = null;
                try{
                    JSONParser parser = new JSONParser();
                    obj = (JSONObject) parser.parse(request);
                    // System.out.println(obj.get("cell0"));

                    String filename = obj.get("file").toString();
                    return_obj = (JSONObject) parser.parse(new FileReader(filename));

                }catch(Exception e){
                    System.out.println(e);
                }
                

                
                StringWriter sw = new StringWriter();
                return_obj.writeJSONString(sw);
                
                String response = sw.toString();

                    
                System.out.println("[200] - response: " + response);
                
                t.sendResponseHeaders(200, response.length());
                OutputStream out = t.getResponseBody();
                out.write(response.getBytes());
                out.close();

            }

        }
    }
    static class IDE implements HttpHandler {
//        @Override
        public void handle(HttpExchange t) throws IOException {
            // String response = ReadHtml("saves/ide.html");\
            // URI uri = ex.getRequestURI();    // public static void main(String[] args) throws Exception
    // {
    //     simpleExample();
    //     twoClassExample();
    //     useLoadedClassExample();
    // }
            String name = new File("saves/ide.html").getName();
            File path = new File("saves/" + name);

            Headers h = t.getResponseHeaders();
            // Could be more clever about the content type based on the filename here.
            h.add("Content-Type", "text/html");

            OutputStream out = t.getResponseBody();

            if (path.exists()) {
                t.sendResponseHeaders(200, path.length());
                out.write(Files.readAllBytes(path.toPath()));

            } else {
                System.err.println("File not found: " + path.getAbsolutePath());
                t.sendResponseHeaders(404, 0);
                out.write("404 File not found.".getBytes());
            }

            out.close();
        }
    }


    static class StaticFiles implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {

            String root = ".";
            URI uri = t.getRequestURI();
            System.out.println("[200] sending - "+ root + uri.getPath());
            String path = uri.getPath();
            File file = new File(root + path).getCanonicalFile();

            if (!file.isFile()) {
                // Object does not exist or is not a file: reject with 404 error.
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                // Object exists and is a file: accept with response code 200.
                String mime = "text/html";
                if(path.substring(path.length()-3).equals(".js")) mime = "application/javascript";
                if(path.substring(path.length()-3).equals("css")) mime = "text/css";            

                Headers h = t.getResponseHeaders();
                h.set("Content-Type", mime);
                t.sendResponseHeaders(200, 0);              

                OutputStream os = t.getResponseBody();
                FileInputStream fs = new FileInputStream(file);
                final byte[] buffer = new byte[0x10000];
                int count = 0;
                while ((count = fs.read(buffer)) >= 0) {
                os.write(buffer,0,count);
                }
                fs.close();
                os.close();
            }  
        }
    }
}


