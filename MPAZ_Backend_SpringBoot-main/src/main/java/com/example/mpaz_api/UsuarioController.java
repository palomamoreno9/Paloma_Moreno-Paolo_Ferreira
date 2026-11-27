package com.example.mpaz_api;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    //prepoblado movido de app aqui de inspectoria debido a que no se permite crear 
    //cuentas de inspectoria desde el registro

    @PostConstruct
    public void initInspectoria() {
        if (!usuarioRepository.existsByEmail("inspectoria@mpaz.cl")) {
            usuarioRepository.save(new Usuario(
                    "Inspectoría General",
                    "inspectoria@mpaz.cl",
                    "inspectoria",
                    "Inspectoria"
            ));
        }
    }

    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario nuevo) {
        String cleanEmail = nuevo.getEmail().trim().toLowerCase();
        if (cleanEmail.contains("inspectoria")) {
            return ResponseEntity.badRequest().body("No se permite crear cuentas de Inspectoría.");
        }
        if (usuarioRepository.existsByEmail(cleanEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Este correo ya está registrado.");
        }
        nuevo.setEmail(cleanEmail);
        if (nuevo.getRol() == null || nuevo.getRol().isBlank()) {
            nuevo.setRol("Solicitante");
        }
        return ResponseEntity.ok(usuarioRepository.save(nuevo));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario credenciales) {
        String cleanEmail = credenciales.getEmail().trim().toLowerCase();
        Optional<Usuario> usuario = usuarioRepository.findByEmailAndPassword(cleanEmail, credenciales.getPassword());
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos.");
    }

    @PutMapping("/{email}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable String email, @RequestBody Usuario datos) {
        Optional<Usuario> existente = usuarioRepository.findByEmail(email.trim().toLowerCase());
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario u = existente.get();
        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            u.setNombre(datos.getNombre().trim());
        }
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            u.setPassword(datos.getPassword().trim());
        }
        return ResponseEntity.ok(usuarioRepository.save(u));
    }
}