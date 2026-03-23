package com.igor.bbb_votacao;

import org.springframework.data.jpa.repository.JpaRepository;

// Essa interface já vem com métodos prontos como "salvar", "deletar" e "buscar todos"
public interface ParticipanteRepository extends JpaRepository<Participante, Integer> {
}