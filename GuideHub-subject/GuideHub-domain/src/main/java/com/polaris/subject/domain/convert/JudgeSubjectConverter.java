package com.polaris.subject.domain.convert;

import com.polaris.subject.domain.entity.SubjectAnswerBO;
import com.polaris.subject.domain.entity.SubjectInfoBO;
import com.polaris.subject.domain.entity.SubjectOptionBO;
import com.polaris.subject.infra.basic.entity.SubjectBrief;
import com.polaris.subject.infra.basic.entity.SubjectJudge;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface JudgeSubjectConverter {

    JudgeSubjectConverter INSTANCE = Mappers.getMapper(JudgeSubjectConverter.class);

    List<SubjectAnswerBO> convertEntityToBoList(List<SubjectJudge> subjectJudgeList);

}
