package com.cloudtech.monitoramento.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MaquinaControllersTest {

    private static final String MAQUINA_JSON = """
            {"nome":"Máquina CNC 01","tipo":"CNC","setor":"Usinagem","status":"em operação"}
            """;

    private static final String PRODUCAO_JSON = """
            {"maquinaId":1,"consumoKw":12.5,"horarioAcionamento":"08:30","pecasProduzidas":150}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void paginaInicialRetorna200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void statusRetornaOnline() throws Exception {
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("online"))
                .andExpect(jsonPath("$.mensagem").value("Aplicação de monitoramento em execução"));
    }

    @Test
    void versaoRetorna100() throws Exception {
        mockMvc.perform(get("/versao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versao").value("1.0.0"));
    }

    @Test
    void cadastrarMaquinaRetorna201() throws Exception {
        mockMvc.perform(post("/maquinas/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAQUINA_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Máquina CNC 01"))
                .andExpect(jsonPath("$.tipo").value("CNC"))
                .andExpect(jsonPath("$.setor").value("Usinagem"))
                .andExpect(jsonPath("$.status").value("em operação"));
    }

    @Test
    void listarMaquinasCadastradas() throws Exception {
        cadastrarMaquina();

        mockMvc.perform(get("/maquinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Máquina CNC 01"));
    }

    @Test
    void atualizarMaquinaRetorna200() throws Exception {
        cadastrarMaquina();

        String dadosAtualizados = """
                {"nome":"Máquina CNC 01","tipo":"CNC","setor":"Usinagem","status":"em manutenção"}
                """;

        mockMvc.perform(put("/maquinas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosAtualizados))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("em manutenção"));
    }

    @Test
    void atualizarMaquinaInexistenteRetorna404() throws Exception {
        mockMvc.perform(put("/maquinas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAQUINA_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void excluirMaquinaRetorna204() throws Exception {
        cadastrarMaquina();

        mockMvc.perform(delete("/maquinas/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/maquinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void excluirMaquinaInexistenteRetorna404() throws Exception {
        mockMvc.perform(delete("/maquinas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void registrarProducaoRetorna201() throws Exception {
        mockMvc.perform(post("/producao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCAO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maquinaId").value(1))
                .andExpect(jsonPath("$.consumoKw").value(12.5))
                .andExpect(jsonPath("$.horarioAcionamento").value("08:30"))
                .andExpect(jsonPath("$.pecasProduzidas").value(150));
    }

    @Test
    void listarProducaoRetornaHistorico() throws Exception {
        mockMvc.perform(post("/producao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCAO_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/producao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].maquinaId").value(1))
                .andExpect(jsonPath("$[0].consumoKw").value(12.5))
                .andExpect(jsonPath("$[0].horarioAcionamento").value("08:30"))
                .andExpect(jsonPath("$[0].pecasProduzidas").value(150));
    }

    private void cadastrarMaquina() throws Exception {
        mockMvc.perform(post("/maquinas/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAQUINA_JSON))
                .andExpect(status().isCreated());
    }
}
