package com.example.biblioteca.controller;

import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusLivro;
import com.example.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepo;

    // Listar todos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("livros", livroRepo.findAll());
        return "livros/list";
    }

    // Listar apenas disponíveis
    @GetMapping("/disponiveis")
    public String listarDisponiveis(Model model) {
        model.addAttribute("livros", livroRepo.findByStatus(StatusLivro.DISPONIVEL));
        return "livros/list";
    }

    // Formulário de novo livro
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("livro", new Livro()); // <-- ESSA LINHA CORRIGE O ERRO 500
        return "livros/form";
    }

    // Salvar livro
    @PostMapping
    public String salvar(@Valid @ModelAttribute("livro") Livro livro, BindingResult br) {
        if (br.hasErrors()) {
            return "livros/form";
        }
        if (livro.getStatus() == null) {
            livro.setStatus(StatusLivro.DISPONIVEL); // todo livro novo começa disponível
        }
        livroRepo.save(livro);
        return "redirect:/livros";
    }

    // Editar livro
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Livro livro = livroRepo.findById(id).orElse(new Livro());
        model.addAttribute("livro", livro);
        return "livros/form";
    }

    // Excluir livro
    @GetMapping("/delete/{id}") // trocado para GET (mais simples de usar com link)
    public String deletar(@PathVariable Long id) {
        livroRepo.deleteById(id);
        return "redirect:/livros";
    }
}