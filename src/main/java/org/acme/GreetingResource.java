package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @Inject
    PersonaProducer producer;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
        public void hello(@QueryParam("name")String name) {
        producer.enviarPersona(name);
    }
}
