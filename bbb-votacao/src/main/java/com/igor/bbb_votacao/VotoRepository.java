package com.igor.bbb_votacao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Integer> {
    Long countByParticipanteId(Integer participanteId);
}