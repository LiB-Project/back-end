package br.edu.ifpb.lib.web.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LevantamentoVO {
    private int ano;
    private long quantidade;
}
