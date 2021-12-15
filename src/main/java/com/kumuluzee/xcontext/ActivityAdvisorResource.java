package com.kumuluzee.xcontext;

import javax.inject.Inject;
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

    @Inject
    private XContext xContext;

    @GET
    public Response getXContext() {
        System.out.println("Resource:");
        System.out.println(xContext.getContext().getLocation().getLatitude());
        return Response.ok().build();
    }

}
