package com.example.biblioteca.controller;

import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepo.findAll());
        return "usuarios/list";
    }

    @GetMapping("/novo")
    public String novo(Usuario usuario) {
        return "usuarios/form";
    }

    @PostMapping
    public String salvar(@Valid Usuario usuario, BindingResult br) {
        if (br.hasErrors()) return "usuarios/form";
        usuarioRepo.save(usuario);
        return "redirect:/usuarios";
    }
}
