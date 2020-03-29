package br.edu.ifpb.lib.web.valueobject;

import br.edu.ifpb.lib.domain.GrandeArea;
import br.edu.ifpb.lib.domain.SubArea;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaEstatisticaVO {
    private String areaBasica;
    private List<SubAreaQuantidade> subAreaQuantidadeList;
}
