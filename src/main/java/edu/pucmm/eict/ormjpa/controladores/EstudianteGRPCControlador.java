package edu.pucmm.eict.ormjpa.controladores;

import edu.pucmm.eict.ormjpa.cliente.EstudianteGRPCClient;
import edu.pucmm.eict.ormjpa.grpc.EstudianteMessage;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class EstudianteGRPCControlador {

    private static EstudianteGRPCClient getClient() {
        return new EstudianteGRPCClient("localhost", 50051);
    }

    public static void listarEstudiantes(Context ctx) {
        EstudianteGRPCClient client = getClient();
        StringBuilder output = new StringBuilder();
        
        try {
            output.append("Listando estudiantes a través de gRPC:\n");
            client.listarEstudiantes();
            
            ctx.contentType("text/plain");
            ctx.result(output.toString());
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("Error al comunicarse con el servidor gRPC: " + e.getMessage());
        } finally {
            try {
                client.shutdown();
            } catch (InterruptedException e) {
                ctx.status(500);
                ctx.result("Error al cerrar el cliente gRPC: " + e.getMessage());
            }
        }
    }

    public static void buscarEstudiante(Context ctx) {
        int matricula = Integer.parseInt(ctx.pathParam("matricula"));
        EstudianteGRPCClient client = getClient();
        
        try {
            EstudianteMessage estudiante = client.consultarEstudiante(matricula);
            if (estudiante != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("matricula", estudiante.getMatricula());
                result.put("nombre", estudiante.getNombre());
                ctx.json(result);
            } else {
                ctx.status(404);
                ctx.result("No se encontró el estudiante con matrícula: " + matricula);
            }
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("Error al comunicarse con el servidor gRPC: " + e.getMessage());
        } finally {
            try {
                client.shutdown();
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    public static void crearEstudiante(Context ctx) {
        int matricula = Integer.parseInt(ctx.formParam("matricula"));
        String nombre = ctx.formParam("nombre");
        
        EstudianteGRPCClient client = getClient();
        try {
            EstudianteMessage estudiante = client.crearEstudiante(matricula, nombre);
            if (estudiante != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("matricula", estudiante.getMatricula());
                result.put("nombre", estudiante.getNombre());
                result.put("mensaje", "Estudiante creado correctamente mediante gRPC");
                ctx.status(201);
                ctx.json(result);
            } else {
                ctx.status(500);
                ctx.result("No se pudo crear el estudiante");
            }
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("Error al comunicarse con el servidor gRPC: " + e.getMessage());
        } finally {
            try {
                client.shutdown();
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    public static void eliminarEstudiante(Context ctx) {
        int matricula = Integer.parseInt(ctx.pathParam("matricula"));
        EstudianteGRPCClient client = getClient();
        
        try {
            boolean resultado = client.borrarEstudiante(matricula);
            if (resultado) {
                ctx.result("Estudiante eliminado correctamente mediante gRPC");
            } else {
                ctx.status(404);
                ctx.result("No se pudo eliminar el estudiante con matrícula: " + matricula);
            }
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("Error al comunicarse con el servidor gRPC: " + e.getMessage());
        } finally {
            try {
                client.shutdown();
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }
}