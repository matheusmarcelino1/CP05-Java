package com.example.biblioteca.controller;

import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusLivro;
import com.example.biblioteca.repository.EmprestimoRepository;
import com.example.biblioteca.repository.LivroRepository;
import com.example.biblioteca.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepo;

    @Autowired
    private LivroRepository livroRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("emprestimos", emprestimoRepo.findByDevolvidoFalse());
        return "emprestimos/list";
    }

    @GetMapping("/novo")
    public String novo(Emprestimo emprestimo, Model model) {
        model.addAttribute("livros", livroRepo.findByStatus(StatusLivro.DISPONIVEL));
        model.addAttribute("usuarios", usuarioRepo.findAll());
        return "emprestimos/form";
    }

    @PostMapping
    public String salvar(@Valid Emprestimo emprestimo, BindingResult br, Model model) {
        if (br.hasErrors() || !emprestimo.isDataPrevistaValida()) {
            if (!emprestimo.isDataPrevistaValida()) br.rejectValue("dataPrevistaDevolucao", "", "Data prevista deve ser posterior à retirada");
            model.addAttribute("livros", livroRepo.findByStatus(StatusLivro.DISPONIVEL));
            model.addAttribute("usuarios", usuarioRepo.findAll());
            return "emprestimos/form";
        }

        Livro l = livroRepo.findById(emprestimo.getLivro().getId()).orElse(null);
        if (l == null || l.getStatus() == StatusLivro.EMPRESTADO) {
            br.rejectValue("livro", "", "Livro indisponível");
            model.addAttribute("livros", livroRepo.findByStatus(StatusLivro.DISPONIVEL));
            model.addAttribute("usuarios", usuarioRepo.findAll());
            return "emprestimos/form";
        }

        l.setStatus(StatusLivro.EMPRESTADO);
        livroRepo.save(l);
        emprestimoRepo.save(emprestimo);
        return "redirect:/emprestimos";
    }

    @PostMapping("/devolver/{id}")
    public String devolver(@PathVariable Long id) {
        Emprestimo e = emprestimoRepo.findById(id).orElse(null);
        if (e != null && !e.isDevolvido()) {
            e.setDevolvido(true);
            Livro l = e.getLivro();
            if (l != null) {
                l.setStatus(StatusLivro.DISPONIVEL);
                livroRepo.save(l);
            }
            emprestimoRepo.save(e);
        }
        return "redirect:/emprestimos";
    }
}
