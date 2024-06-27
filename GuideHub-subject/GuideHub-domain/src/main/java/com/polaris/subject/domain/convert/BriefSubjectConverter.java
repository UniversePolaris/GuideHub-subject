package com.polaris.subject.domain.convert;

import com.polaris.subject.domain.entity.SubjectAnswerBO;
import com.polaris.subject.domain.entity.SubjectInfoBO;
import com.polaris.subject.infra.basic.entity.SubjectBrief;
import com.polaris.subject.infra.basic.entity.SubjectMultiple;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BriefSubjectConverter {

    BriefSubjectConverter INSTANCE = Mappers.getMapper(BriefSubjectConverter.class);

    SubjectBrief convertBoToEntity(SubjectInfoBO subjectInfoBO);

}
