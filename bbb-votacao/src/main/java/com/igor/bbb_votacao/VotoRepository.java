package com.igor.bbb_votacao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface VotoRepository extends JpaRepository<Voto, Integer> {
    Long countByParticipanteId(Integer participanteId);

    @Transactional
    void deleteByParticipanteId(Integer participanteId);
    // Adicione esta linha junto com as outras que já existem aí
    List<Voto> findByUsuarioId(Integer usuarioId);

    
}