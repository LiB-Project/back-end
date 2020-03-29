package br.edu.ifpb.lib.web.valueobject;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Builder
@Data
public class QueryVO {
    private String searchText;
    private Pageable pageable;
    private List<QueryRelevanceVO> queryRelevanceList;
}
