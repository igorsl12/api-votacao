-- 1. Cria o banco de dados (se ele não existir) e entra nele
CREATE DATABASE IF NOT EXISTS bbb_votacao;
USE bbb_votacao;

-- 2. Tabela de Participantes (Independente)
CREATE TABLE participante (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

-- 3. Tabela de Usuários (Independente)
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL
);

-- 4. Tabela de Votos (Depende de Participante e Usuário)
CREATE TABLE voto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    participante_id INT NOT NULL,
    usuario_id INT NOT NULL,
    FOREIGN KEY (participante_id) REFERENCES participante(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- 5. Inserir os dados iniciais para o sistema já nascer funcionando
INSERT INTO participante (nome) VALUES ('Participante 1'), ('Participante 2');

INSERT INTO usuario (nome, email, senha, perfil) 
VALUES 
('Igor Silva Lima', 'admin@bbb.com', 'admin', 'ADMIN'),
('Eleitor Comum', 'eleitor@bbb.com', '123456', 'ELEITOR');