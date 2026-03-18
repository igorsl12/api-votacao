package com.igor.bbb_votacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface VotoRepository extends JpaRepository<Voto, Integer> {
    Long countByParticipanteId(Integer participanteId);

    @Transactional
    void deleteByParticipanteId(Integer participanteId);
}