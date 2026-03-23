package com.igor.bbb_votacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/arquivos")
@CrossOrigin(origins = "*")
public class ArquivoController {

    // Nome da pasta onde as fotos vão ficar
    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<?> fazerUpload(@RequestParam("foto") MultipartFile arquivo) {
        try {
            // 1. Cria a pasta "uploads" no projeto se ela não existir
            File pasta = new File(UPLOAD_DIR);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            // 2. Gera um nome único para a imagem não substituir outra (ex: 8f3a_foto.jpg)
            String nomeUnico = UUID.randomUUID().toString() + "_" + arquivo.getOriginalFilename();
            Path caminhoDoArquivo = Paths.get(UPLOAD_DIR + nomeUnico);

            // 3. Salva o arquivo fisicamente no computador
            Files.write(caminhoDoArquivo, arquivo.getBytes());

            // 4. Monta a URL mágica que o nosso WebConfig liberou no Passo 1
            String urlDaFoto = "http://localhost:8081/uploads/" + nomeUnico;

            // Devolve a URL em formato JSON
            return ResponseEntity.ok().body("{\"url\": \"" + urlDaFoto + "\"}");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao salvar a imagem.");
        }
    }
}