package br.edu.ifpb.lib.web.valueobject;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PublicacaoRecenteVO {
    private String id;
    private String titulo;
}
