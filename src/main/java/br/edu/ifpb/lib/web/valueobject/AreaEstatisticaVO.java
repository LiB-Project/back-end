package br.edu.ifpb.lib.web.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaEstatisticaVO {
    private String areaBasica;
    private List<SubAreaQuantidade> subAreaQuantidadeList;
}
