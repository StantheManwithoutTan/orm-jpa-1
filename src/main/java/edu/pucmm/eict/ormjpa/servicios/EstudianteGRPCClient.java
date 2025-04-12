package edu.pucmm.eict.ormjpa.servicios;

import edu.pucmm.eict.ormjpa.grpc.*; // Corregir este import para incluir las clases generadas
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class EstudianteGRPCClient {
    private final ManagedChannel channel;
    private final EstudianteServiceGrpc.EstudianteServiceBlockingStub blockingStub;

    public EstudianteGRPCClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // Solo para desarrollo, en producción usar TLS
                .build();
        blockingStub = EstudianteServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // Listar todos los estudiantes
    public void listarEstudiantes() {
        ListarEstudiantesRequest request = ListarEstudiantesRequest.newBuilder().build();
        ListarEstudiantesResponse response;
        
        try {
            response = blockingStub.listarEstudiantes(request);
            System.out.println("Estudiantes recibidos: " + response.getEstudiantesCount());
            for (EstudianteMessage estudiante : response.getEstudiantesList()) {
                System.out.println("Matricula: " + estudiante.getMatricula() + ", Nombre: " + estudiante.getNombre());
            }
        } catch (Exception e) {
            System.err.println("Error RPC: " + e.getMessage());
        }
    }

    // Consultar un estudiante por matrícula
    public EstudianteMessage consultarEstudiante(int matricula) {
        ConsultarEstudianteRequest request = ConsultarEstudianteRequest.newBuilder()
                .setMatricula(matricula)
                .build();
        
        try {
            ConsultarEstudianteResponse response = blockingStub.consultarEstudiante(request);
            if (response.hasEstudiante()) {
                EstudianteMessage estudiante = response.getEstudiante();
                System.out.println("Estudiante encontrado - Matricula: " + estudiante.getMatricula() + 
                                   ", Nombre: " + estudiante.getNombre());
                return estudiante;
            } else {
                System.out.println("No se encontró estudiante con matrícula: " + matricula);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error RPC: " + e.getMessage());
            return null;
        }
    }

    // Crear un nuevo estudiante
    public EstudianteMessage crearEstudiante(int matricula, String nombre) {
        EstudianteMessage estudiante = EstudianteMessage.newBuilder()
                .setMatricula(matricula)
                .setNombre(nombre)
                .build();
                
        CrearEstudianteRequest request = CrearEstudianteRequest.newBuilder()
                .setEstudiante(estudiante)
                .build();
                
        try {
            CrearEstudianteResponse response = blockingStub.crearEstudiante(request);
            if (response.getExito()) {
                System.out.println("Estudiante creado correctamente - Matricula: " + 
                                   response.getEstudiante().getMatricula() + 
                                   ", Nombre: " + response.getEstudiante().getNombre());
                return response.getEstudiante();
            } else {
                System.out.println("No se pudo crear el estudiante");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error RPC: " + e.getMessage());
            return null;
        }
    }

    // Borrar un estudiante
    public boolean borrarEstudiante(int matricula) {
        BorrarEstudianteRequest request = BorrarEstudianteRequest.newBuilder()
                .setMatricula(matricula)
                .build();
                
        try {
            BorrarEstudianteResponse response = blockingStub.borrarEstudiante(request);
            if (response.getExito()) {
                System.out.println("Estudiante con matrícula " + matricula + " eliminado correctamente");
                return true;
            } else {
                System.out.println("No se pudo eliminar el estudiante con matrícula " + matricula);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error RPC: " + e.getMessage());
            return false;
        }
    }

    // Método principal para demostrar el uso del cliente
    public static void main(String[] args) throws InterruptedException {
        EstudianteGRPCClient client = new EstudianteGRPCClient("localhost", 50051);
        try {
            // Listar estudiantes existentes
            System.out.println("== LISTANDO TODOS LOS ESTUDIANTES ==");
            client.listarEstudiantes();
            
            // Crear un nuevo estudiante
            System.out.println("\n== CREANDO ESTUDIANTE NUEVO ==");
            int nuevaMatricula = 99999;
            client.crearEstudiante(nuevaMatricula, "Estudiante de Prueba gRPC");
            
            // Consultar el estudiante creado
            System.out.println("\n== CONSULTANDO ESTUDIANTE CREADO ==");
            client.consultarEstudiante(nuevaMatricula);
            
            // Borrar el estudiante
            System.out.println("\n== BORRANDO ESTUDIANTE ==");
            client.borrarEstudiante(nuevaMatricula);
            
            // Verificar que se borró
            System.out.println("\n== VERIFICANDO BORRADO ==");
            client.consultarEstudiante(nuevaMatricula);
            
            // Listar nuevamente
            System.out.println("\n== LISTANDO ESTUDIANTES DESPUÉS DE OPERACIONES ==");
            client.listarEstudiantes();
        } finally {
            client.shutdown();
        }
    }
}
