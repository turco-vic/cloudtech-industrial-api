package com.cloudtech.monitoramento.controller;

import com.cloudtech.monitoramento.models.Maquinas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MaquinaControllers {

    private List<Maquinas> maquinas = new ArrayList<>();
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
}
