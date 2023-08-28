package com.ibm.hospedagem.repository;

import com.ibm.hospedagem.model.Reserva;
import com.ibm.hospedagem.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByStatus(Status status);
    List<Reserva> findByDataInicioBetweenOrDataFimBetween(LocalDate dataInicioHospedagem, LocalDate dataFimHospedagem, LocalDate dataInicioVerificacaoBanco, LocalDate dataFimVerificacaoBanco);
}
