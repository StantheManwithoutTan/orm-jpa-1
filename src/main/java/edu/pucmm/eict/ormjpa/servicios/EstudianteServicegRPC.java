package edu.pucmm.eict.ormjpa.servicios;

import edu.pucmm.eict.ormjpa.entidades.Estudiante;
import edu.pucmm.eict.ormjpa.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class EstudianteServicegRPC extends EstudianteServiceGrpc.EstudianteServiceImplBase {

    @Override
    public void listarEstudiantes(ListarEstudiantesRequest request, StreamObserver<ListarEstudiantesResponse> responseObserver) {
        try {
            List<Estudiante> estudiantes = EstudianteServices.getInstancia().findAll();
            
            ListarEstudiantesResponse.Builder response = ListarEstudiantesResponse.newBuilder();
            for (Estudiante estudiante : estudiantes) {
                EstudianteMessage estudianteMessage = EstudianteMessage.newBuilder()
                        .setMatricula(estudiante.getMatricula())
                        .setNombre(estudiante.getNombre())
                        .build();
                response.addEstudiantes(estudianteMessage);
            }
            
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void consultarEstudiante(ConsultarEstudianteRequest request, StreamObserver<ConsultarEstudianteResponse> responseObserver) {
        try {
            Estudiante estudiante = EstudianteServices.getInstancia().find(request.getMatricula());
            
            ConsultarEstudianteResponse.Builder response = ConsultarEstudianteResponse.newBuilder();
            if (estudiante != null) {
                EstudianteMessage estudianteMessage = EstudianteMessage.newBuilder()
                        .setMatricula(estudiante.getMatricula())
                        .setNombre(estudiante.getNombre())
                        .build();
                response.setEstudiante(estudianteMessage);
            }
            
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void crearEstudiante(CrearEstudianteRequest request, StreamObserver<CrearEstudianteResponse> responseObserver) {
        try {
            EstudianteMessage estudianteMsg = request.getEstudiante();
            
            Estudiante nuevoEstudiante = new Estudiante(estudianteMsg.getMatricula(), estudianteMsg.getNombre());
            Estudiante estudianteCreado = EstudianteServices.getInstancia().crear(nuevoEstudiante);
            
            CrearEstudianteResponse response = CrearEstudianteResponse.newBuilder()
                    .setEstudiante(EstudianteMessage.newBuilder()
                            .setMatricula(estudianteCreado.getMatricula())
                            .setNombre(estudianteCreado.getNombre())
                            .build())
                    .setExito(true)
                    .build();
                    
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void borrarEstudiante(BorrarEstudianteRequest request, StreamObserver<BorrarEstudianteResponse> responseObserver) {
        try {
            boolean resultado = EstudianteServices.getInstancia().eliminar(request.getMatricula());
            
            BorrarEstudianteResponse response = BorrarEstudianteResponse.newBuilder()
                    .setExito(resultado)
                    .build();
                    
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}