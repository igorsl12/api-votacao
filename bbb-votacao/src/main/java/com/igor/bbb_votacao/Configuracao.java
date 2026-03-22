package com.igor.bbb_votacao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Configuracao {
    
    @Id
    private Integer id = 1; // Teremos sempre apenas a configuração de ID 1
    
    private boolean votacaoAberta = true; // Por padrão, a votação começa aberta

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public boolean isVotacaoAberta() { return votacaoAberta; }
    public void setVotacaoAberta(boolean votacaoAberta) { this.votacaoAberta = votacaoAberta; }
}