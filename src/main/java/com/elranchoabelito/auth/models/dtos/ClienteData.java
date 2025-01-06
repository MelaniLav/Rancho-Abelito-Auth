package com.elranchoabelito.auth.models.dtos;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteData {

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String direccion;
    private String documento;
    private Long cuentaId;
    private Date fechaNacimiento;

}
