package com.ibm.hospedagem.repository;

import com.ibm.hospedagem.model.Hospedagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospedagemRepository extends JpaRepository<Hospedagem, Long> {
}
