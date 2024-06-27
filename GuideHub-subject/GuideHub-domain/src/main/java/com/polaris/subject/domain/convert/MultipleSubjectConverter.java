package com.polaris.subject.domain.convert;

import com.polaris.subject.domain.entity.SubjectAnswerBO;
import com.polaris.subject.infra.basic.entity.SubjectJudge;
import com.polaris.subject.infra.basic.entity.SubjectMultiple;
import com.polaris.subject.infra.basic.entity.SubjectRadio;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MultipleSubjectConverter {

    MultipleSubjectConverter INSTANCE = Mappers.getMapper(MultipleSubjectConverter.class);

    SubjectMultiple convertBoToEntity(SubjectAnswerBO subjectAnswerBO);

    List<SubjectAnswerBO> convertEntityToBoList(List<SubjectMultiple> subjectMultipleList);


}
