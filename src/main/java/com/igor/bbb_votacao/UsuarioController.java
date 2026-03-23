package com.igor.bbb_votacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // NOVO: Para receber arquivos

import java.nio.file.Files; // NOVO: Para manipular arquivos no disco
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID; // NOVO: Para gerar nomes únicos

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository repository;
    
    // NOVO: Constante que define a pasta onde as fotos serão salvas
    private static final String UPLOAD_DIR = "uploads/";

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    // 1. CADASTRAR USUÁRIO
    @PostMapping
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario novoUsuario) {
        novoUsuario.setPerfil("ELEITOR");

        boolean emailJaExiste = repository.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(novoUsuario.getEmail()));

        if (emailJaExiste) {
            return ResponseEntity.status(400).body("E-mail já cadastrado");
        }

        try {
            Usuario usuarioSalvo = repository.save(novoUsuario);
            return ResponseEntity.ok(usuarioSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor");
        }
    }

    // 2. ATUALIZAR NOME DO USUÁRIO
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarNomeUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioAtualizado) {
        try {
            Usuario usuarioExistente = repository.findById(id).orElseThrow();
            usuarioExistente.setNome(usuarioAtualizado.getNome());
            repository.save(usuarioExistente);
            
            return ResponseEntity.ok("Nome atualizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao atualizar o usuário.");
        }
    }

    // 3. NOVA ROTA: BUSCAR USUÁRIO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Integer id) {
        try {
            // Vai no banco e pega o usuário completo
            Usuario usuario = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }
    }

    // ==========================================
    // NOVA ROTA: ALTERAR SENHA
    // ==========================================
    @PutMapping("/{id}/senha")
    public ResponseEntity<?> alterarSenha(@PathVariable Integer id, @RequestBody Map<String, String> senhas) {
        try {
            Usuario usuario = repository.findById(id).orElseThrow();
            
            String senhaAtual = senhas.get("senhaAtual");
            String novaSenha = senhas.get("novaSenha");

            // Verifica se a senha atual que ele digitou bate com a do banco
            if (!usuario.getSenha().equals(senhaAtual)) {
                return ResponseEntity.status(400).body("Senha atual incorreta.");
            }

            // Se bater, atualiza para a nova
            usuario.setSenha(novaSenha);
            repository.save(usuario);
            
            return ResponseEntity.ok("Senha alterada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Usuário não encontrado.");
        }
    }

    // ==========================================
    // ROTA: EXCLUIR CONTA
    // ==========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirConta(@PathVariable Integer id) {
        try {
            repository.deleteById(id);
            return ResponseEntity.ok("Conta excluída com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao excluir conta.");
        }
    }

    // ==========================================
    // NOVA ROTA: UPLOAD DE FOTO DE PERFIL
    // ==========================================
    @PostMapping("/{id}/foto")
    public ResponseEntity<?> fazerUploadFoto(@PathVariable Integer id, @RequestParam("arquivoFoto") MultipartFile arquivo) {
        
        if (arquivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum arquivo foi selecionado.");
        }

        try {
            Usuario usuarioDB = repository.findById(id).orElseThrow();

            // Cria a pasta uploads/ se ela não existir
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Cria um nome único para o arquivo
            String extensao = arquivo.getOriginalFilename().substring(arquivo.getOriginalFilename().lastIndexOf("."));
            String nomeUnicoArquivo = "foto_perfil_" + id + "_" + UUID.randomUUID().toString().substring(0, 8) + extensao;

            // Salva o arquivo no disco
            Path destination = uploadPath.resolve(nomeUnicoArquivo);
            Files.copy(arquivo.getInputStream(), destination);

            // Apaga a foto antiga se existir
            if (usuarioDB.getFoto() != null) {
                try {
                    Files.deleteIfExists(uploadPath.resolve(usuarioDB.getFoto()));
                } catch (Exception e) { System.err.println("Erro ao deletar foto antiga."); }
            }

            // Salva o nome da nova foto no banco de dados
            usuarioDB.setFoto(nomeUnicoArquivo);
            repository.save(usuarioDB);

            // Devolve a URL completa para o front-end exibir a imagem na hora
            return ResponseEntity.ok(Map.of("urlCompleta", "http://localhost:8081/images/" + nomeUnicoArquivo));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno ao processar upload: " + e.getMessage());
        }
    }
}