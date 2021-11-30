package com.kumuluzee.xcontext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class ActivityAdvisorResource {

    @Context
    private HttpServletRequest httpServletRequest;

    @GET
    public Response getXContext() {
        String xContent = httpServletRequest.getHeader("X-Context");
        System.out.println(xContent);
        return Response.ok().build();
    }

}
