package fplhn.udpm.examdistribution.core.headsubject.examrule.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindSubjectRequest extends PageableRequest {

    private String valueSearch;

    private String examRuleId;

}
