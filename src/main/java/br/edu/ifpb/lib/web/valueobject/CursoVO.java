package br.edu.ifpb.lib.web.valueobject;

import lombok.Data;
import br.edu.ifpb.lib.domain.GrandeArea;
import br.edu.ifpb.lib.domain.NivelCurso;

import java.io.Serializable;

@Data
public class CursoVO implements Serializable {
    private String id;
    private String sigla;
    private String nome;
    private String descricao;
    private NivelCurso nivel;
    private GrandeArea grandeArea;
}
