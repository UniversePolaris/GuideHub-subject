package com.polaris.subject.infra.basic.service;

import com.polaris.subject.common.entity.PageResult;
import com.polaris.subject.infra.basic.entity.SubjectInfoEs;

public interface SubjectEsService {

    boolean insert(SubjectInfoEs subjectInfoEs);

    PageResult<SubjectInfoEs> querySubjectList(SubjectInfoEs subjectInfoEs);

}
