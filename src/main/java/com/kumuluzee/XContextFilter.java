package com.kumuluzee;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import java.io.IOException;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequestScoped
@Provider
public class XContextFilter implements ContainerRequestFilter {

    @Inject
    private XContext xContext;

    public void filter(ContainerRequestContext reqContext) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String xContextJSON = reqContext.getHeaderString("X-Context");
        Context localContext = objectMapper.readValue(xContextJSON, Context.class);
        xContext.setContext(localContext);
    }
}
