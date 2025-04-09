package com.techdistribuidora.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import com.techdistribuidora.processors.CsvValidationProcessor; // Asegúrate de importar el processor

@Component
public class FileTransferRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // Manejo de excepciones en caso de error de validación
        onException(Exception.class)
            .handled(true)
            .log(">>> Error en archivo ${file:name}: ${exception.message}")
            .to("file:C:/Users/marti/Downloads/cvs_error?autoCreate=true");

        // Ruta: lee archivos, valida encabezado y en función del tipo de cliente, envía a subcarpetas
        from("file:C:/Users/marti/Downloads/cvs?include=.*\\.csv&noop=true")
            .log(">>> Iniciando procesamiento del archivo: ${file:name}")
            .process(new CsvValidationProcessor())
            .log(">>> Archivo ${file:name} válido. Tipo de cliente: ${header.tipoCliente}")
            .choice()
                .when(header("tipoCliente").isEqualTo("VIP"))
                    .to("file:C:/Users/marti/Downloads/cvs_output/VIP?autoCreate=true")
                .otherwise()
                    .to("file:C:/Users/marti/Downloads/cvs_output/regular?autoCreate=true")
            .log(">>> Archivo ${file:name} copiado a carpeta: ${header.tipoCliente}");
    }
}
