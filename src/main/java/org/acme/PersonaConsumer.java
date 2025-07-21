package org.acme;


import com.ejemplo.Persona;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class PersonaConsumer {

    @Incoming("persona-consumer")
    public void consumir(byte[] data) throws IOException {
        DatumReader<Persona> reader = new SpecificDatumReader<>(Persona.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        Persona persona = reader.read(null, decoder);
        System.out.println("Nombre recibido: " + persona.getNombre());
    }
}
