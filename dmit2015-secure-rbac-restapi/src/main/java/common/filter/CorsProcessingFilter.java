package common.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * CORS (Cross-Origin Resource Sharing) is use to enable web applications to access resources from different origins.
 *
 * To inform the browser which origins are allowed and what request headers the client can pass to the server.
 *
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS">Cross Origin Resource Sharing (CORS)</a>
 *
 */
@Provider
@Priority(100_000)
public class CorsProcessingFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            responseContext.setStatus(200);
        }

        if (responseContext.getHeaderString("Access-Control-Allow-Origin") == null) {
            MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            headers.add("Access-Control-Allow-Origin","*"); // allow requests from any origin
            headers.add("Access-Control-Allow-Credentials", "true");    // allow credentials to be included
            headers.add("Access-Control-Allow-Headers","Origin, Content-Type, Accept, Authorization");  // allow custom headers
            headers.add("Access-Control-Allow-Methods","GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD"); // allowed HTTP methods
        }
    }
}