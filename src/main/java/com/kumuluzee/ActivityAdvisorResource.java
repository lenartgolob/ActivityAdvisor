package com.kumuluzee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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

    @Inject
    private ActivityAdvisorService advisorBean;

    @Operation(description = "Returns random activity based on the parameters sent in X-Context header.", summary = "Get a fun activity near you", tags = "Activities", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Get a fun activity near you",
                    content = @Content(schema = @Schema(implementation = ActivityResponse.class)),
                    headers = {@Header(name = "X-Context", schema = @Schema(implementation = Context.class))}
            )})
    @Parameter(in = ParameterIn.HEADER, description = "Custom Header To be Pass", name = "X-Context", required = true, content = @Content(schema = @Schema(implementation = Context.class)))
    @GET
    public Response getXContext() throws Exception {
        ActivityResponse activityResponse = advisorBean.getActivity();
        return activityResponse != null
                ? Response.ok(activityResponse).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

}
