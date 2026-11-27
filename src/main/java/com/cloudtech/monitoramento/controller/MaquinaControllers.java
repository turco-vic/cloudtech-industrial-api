package com.cloudtech.monitoramento.controller;

import com.cloudtech.monitoramento.models.Maquinas;
import com.cloudtech.monitoramento.models.Producao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MaquinaControllers {

    private List<Maquinas> maquinas = new ArrayList<>();
    private List<Producao> producoes = new ArrayList<>();
    private long proximoId = 1;

    @GetMapping("/maquinas")
    public List<Maquinas> listarMaquinas() {
        return maquinas;
    }

    @PostMapping("/maquinas/cadastro")
    public ResponseEntity<Maquinas> cadastrarMaquina(@RequestBody Maquinas maquina) {
        maquina.setId(proximoId);
        proximoId++;
        maquinas.add(maquina);
        return ResponseEntity.status(HttpStatus.CREATED).body(maquina);
    }

    @PutMapping("/maquinas/{id}")
    public ResponseEntity<Maquinas> atualizarMaquina(@PathVariable Long id, @RequestBody Maquinas dados) {
        for (Maquinas maquina : maquinas) {
            if (maquina.getId().equals(id)) {
                maquina.setNome(dados.getNome());
                maquina.setTipo(dados.getTipo());
                maquina.setSetor(dados.getSetor());
                maquina.setStatus(dados.getStatus());
                return ResponseEntity.ok(maquina);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/maquinas/{id}")
    public ResponseEntity<Void> excluirMaquina(@PathVariable Long id) {
        for (Maquinas maquina : maquinas) {
            if (maquina.getId().equals(id)) {
                maquinas.remove(maquina);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/producao")
    public List<Producao> listarProducao() {
        return producoes;
    }

    @PostMapping("/producao")
    public ResponseEntity<Producao> registrarProducao(@RequestBody Producao producao) {
        producoes.add(producao);
        return ResponseEntity.status(HttpStatus.CREATED).body(producao);
    }
}
