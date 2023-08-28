package com.ibm.hospedagem.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ibm.hospedagem.service.exception.reservaException.ReservaBadRequestException;

public enum Status {
    CONFIRMADO,
    PENDENTE,
    CANCELADO;

    @JsonCreator
    public static Status fromValue(String value) {

        for (Status status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new ReservaBadRequestException("Status inválido (" + value + ") somente são aceitos os valores CONFIRMADO ou PENDENTE");
    }

}
