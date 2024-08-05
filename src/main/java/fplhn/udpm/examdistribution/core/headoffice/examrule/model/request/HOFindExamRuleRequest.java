package fplhn.udpm.examdistribution.core.headoffice.examrule.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HOFindExamRuleRequest extends PageableRequest {

    private String valueSearch;

}
