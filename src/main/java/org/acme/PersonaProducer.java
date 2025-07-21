package org.acme;

import com.ejemplo.Persona;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.io.IOException;

@ApplicationScoped
public class PersonaProducer {

    @Inject
    @Channel("persona-producer")
    Emitter<byte[]> emitter;

    public void enviarPersona(String nombre) {
        try {
        Persona persona = new Persona();
        persona.setNombre(nombre);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        DatumWriter<Persona> writer = new SpecificDatumWriter<>(Persona.class);
        writer.write(persona, encoder);
        encoder.flush();

        emitter.send(out.toByteArray());
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}