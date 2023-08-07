package com.ibm.hospedagem.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemBadRequestException;

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
        throw new HospedagemBadRequestException("Status inválido (" + value + ") somente são aceitos os valores CONFIRMADO ou PENDENTE");
    }

}
