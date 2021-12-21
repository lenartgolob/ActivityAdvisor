package com.kumuluzee.xcontext;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class ActivityAdvisorResource {

    @Inject
    private XContext xContext;

    @Inject
    private ActivityAdvisorAPI advisorBean;

    @GET
    public Response getXContext() throws Exception {
        ActivityResponse activityResponse = advisorBean.getActivity();
        return activityResponse != null
                ? Response.ok(activityResponse).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

}
