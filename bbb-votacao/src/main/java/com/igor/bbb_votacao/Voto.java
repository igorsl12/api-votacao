package com.igor.bbb_votacao;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Voto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Pega a data e hora exata do momento do voto
    private LocalDateTime dataHora = LocalDateTime.now();

    // Diz ao Java que "Muitos votos pertencem a Um participante"
    @ManyToOne
    @JoinColumn(name = "participante_id")
    private Participante participante;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Participante getParticipante() { return participante; }
    public void setParticipante(Participante participante) { this.participante = participante; }
}