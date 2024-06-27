package com.polaris.subject.domain.convert;

import com.polaris.subject.domain.entity.SubjectAnswerBO;
import com.polaris.subject.domain.entity.SubjectInfoBO;
import com.polaris.subject.infra.basic.entity.SubjectInfo;
import com.polaris.subject.infra.basic.entity.SubjectMultiple;
import com.polaris.subject.infra.basic.entity.SubjectRadio;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RadioSubjectConverter {

    RadioSubjectConverter INSTANCE = Mappers.getMapper(RadioSubjectConverter.class);

    SubjectRadio convertBoToEntity(SubjectAnswerBO subjectAnswerBO);

    List<SubjectAnswerBO> convertEntityToBoList(List<SubjectRadio> subjectRadioList);

}
