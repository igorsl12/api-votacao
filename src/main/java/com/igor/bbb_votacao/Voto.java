package com.igor.bbb_votacao;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votos")
public class Voto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Pega a data e hora exata do momento do voto
    private LocalDateTime dataHora = LocalDateTime.now();

    // Novo campo para o histórico (Começa como true por padrão)
    private boolean visivelNoHistorico = true;

    // Diz ao Java que "Muitos votos pertencem a Um participante"
    @ManyToOne
    @JoinColumn(name = "participante_id")
    private Participante participante;

    // Diz ao Java que "Muitos votos pertencem a Um usuário"
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // --- GETTERS E SETTERS ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Participante getParticipante() { return participante; }
    public void setParticipante(Participante participante) { this.participante = participante; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // GETTER E SETTER DO NOVO CAMPO (Essencial para o Build funcionar)
    public boolean isVisivelNoHistorico() {
        return visivelNoHistorico;
    }

    public void setVisivelNoHistorico(boolean visivelNoHistorico) {
        this.visivelNoHistorico = visivelNoHistorico;
    }
}