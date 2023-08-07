package com.ibm.hospedagem.repository;

import com.ibm.hospedagem.model.Hospedagem;
import com.ibm.hospedagem.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HospedagemRepository extends JpaRepository<Hospedagem, Long> {
    List<Hospedagem> findByStatus(Status status);
    List<Hospedagem> findByDataInicioBetweenOrDataFimBetween(LocalDate dataInicioHospedagem, LocalDate dataFimHospedagem, LocalDate dataInicioVerificacaoBanco, LocalDate dataFimVerificacaoBanco);
}
