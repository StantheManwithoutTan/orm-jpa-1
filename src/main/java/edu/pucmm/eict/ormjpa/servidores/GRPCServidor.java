package edu.pucmm.eict.ormjpa.servidores;

import edu.pucmm.eict.ormjpa.servicios.EstudianteServicegRPC; // Añadir esta importación
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GRPCServidor {
    private Server server;
    private int port = 50051; // Puerto por defecto para gRPC

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new EstudianteServicegRPC())
                .build()
                .start();
        
        System.out.println("Servidor gRPC iniciado, escuchando en el puerto " + port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando servidor gRPC...");
            this.stop();
            System.out.println("Servidor gRPC detenido.");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}