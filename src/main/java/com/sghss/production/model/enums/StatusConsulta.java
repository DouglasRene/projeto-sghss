// src/main/java/com/sghss/production/model/enums/StatusConsulta.java
package com.sghss.production.model.enums;

public enum StatusConsulta {
    AGENDADA,      // Consulta recém-agendada
    CONFIRMADA,    // Paciente ou sistema confirmou a presença
    CANCELADA,     // Consulta cancelada pelo paciente ou profissional
    REALIZADA,     // Consulta que já ocorreu
    REAGENDADA     // Consulta que foi movida para outra data/hora
}