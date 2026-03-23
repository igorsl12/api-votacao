package com.igor.bbb_votacao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column; // Importação necessária para definir o tamanho da coluna

@Entity // Avisa o Java que isso é uma tabela do banco
public class Participante {
    
    @Id // Avisa que o ID é a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco gera o ID sozinho
    private Integer id;
    
    private String nome;

    // NOVO ATRIBUTO: Guarda o link da foto da internet
    // Colocamos length = 1000 porque links de imagens podem ser bem longos
    @Column(length = 1000) 
    private String urlFoto;

    // Getters e Setters (Para permitir que o Java leia e grave os dados)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    // Getters e Setters da foto
    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }
}