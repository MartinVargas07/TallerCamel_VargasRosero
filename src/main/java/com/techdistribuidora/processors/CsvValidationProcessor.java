package com.techdistribuidora.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CsvValidationProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        // Obtener el InputStream del cuerpo del mensaje (el archivo CSV)
        InputStream is = exchange.getIn().getBody(InputStream.class);
        
        // En caso de requerir leer más de una vez, activa stream caching en tu CamelContext.
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        
        // Leer la primera línea del archivo para obtener el encabezado.
        String header = reader.readLine();
        
        if (header == null || !header.contains("order_id")) {
            // Si el encabezado es nulo o no contiene "order_id" (puedes ajustar este criterio),
            // lanzamos una excepción para indicar que el archivo es inválido.
            throw new Exception("Archivo sin encabezado o encabezado inválido");
        }
        
        // Leer la primera línea de datos para extraer la información del tipo de cliente.
        String firstDataLine = reader.readLine();
        if (firstDataLine == null) {
            throw new Exception("Archivo sin datos");
        }
        
        // Suponiendo que el formato del encabezado es:
        // order_id,customer,tipoCliente,product,quantity,price
        // Separamos el encabezado y la primera línea de datos en arrays
        String[] headerColumns = header.split(",");
        String[] dataColumns = firstDataLine.split(",");
        
        // Buscar en el encabezado el índice de la columna "tipoCliente"
        int tipoIndex = -1;
        for (int i = 0; i < headerColumns.length; i++) {
            if (headerColumns[i].trim().equalsIgnoreCase("tipoCliente")) {
                tipoIndex = i;
                break;
            }
        }
        
        if (tipoIndex == -1) {
            throw new Exception("Encabezado no contiene 'tipoCliente'");
        }
        
        // Extraer el valor del campo "tipoCliente" de la primera línea de datos.
        String tipoCliente = dataColumns[tipoIndex].trim();
        
        // Guardar el valor en el header del Exchange, para usarlo en la ruta.
        exchange.getIn().setHeader("tipoCliente", tipoCliente);
        
        // NOTA: Si necesitas volver a leer el contenido del archivo en otros pasos de la ruta,
        // considera habilitar Stream Caching (por ejemplo, con getContext().setStreamCaching(true))
        // o almacenar el contenido completo en una variable y volverlo a inyectar.
    }
}
