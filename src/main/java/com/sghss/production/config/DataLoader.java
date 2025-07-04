package com.sghss.production.config;

import com.sghss.production.model.Perfil;
import com.sghss.production.model.Usuario;
import com.sghss.production.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Configuration
@Profile("dev") // Só executa no perfil 'dev'
public class DataLoader {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByUsername("admin@sghss.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin@sghss.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // Senha padrão para dev
                admin.setPerfis(Set.of(Perfil.ROLE_ADMIN));
                usuarioRepository.save(admin);
                System.out.println("Usuário ADMIN padrão criado!");
            }
        };
    }
}