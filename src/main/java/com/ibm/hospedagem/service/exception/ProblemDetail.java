package com.ibm.hospedagem.service.exception;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDetail {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String timestamp;

}