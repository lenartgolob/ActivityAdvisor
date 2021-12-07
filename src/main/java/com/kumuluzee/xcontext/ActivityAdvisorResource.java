package com.kumuluzee.xcontext;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class ActivityAdvisorResource {

    @GET
    public Response getXContext(@HeaderParam("X-Context") String xContext) {
        System.out.println(xContext);
        return Response.ok().build();
    }

}
