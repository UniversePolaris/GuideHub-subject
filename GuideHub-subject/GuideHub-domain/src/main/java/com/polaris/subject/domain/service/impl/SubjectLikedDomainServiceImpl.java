package com.polaris.subject.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.polaris.subject.common.entity.PageResult;
import com.polaris.subject.common.enums.IsDeletedFlagEnum;
import com.polaris.subject.common.enums.SubjectLikedStatusEnum;
import com.polaris.subject.common.util.LoginUtil;
import com.polaris.subject.domain.convert.SubjectInfoConverter;
import com.polaris.subject.domain.convert.SubjectLikedBOConverter;
import com.polaris.subject.domain.entity.SubjectInfoBO;
import com.polaris.subject.domain.entity.SubjectLikedBO;
import com.polaris.subject.domain.entity.SubjectLikedMessage;
import com.polaris.subject.domain.redis.RedisUtil;
import com.polaris.subject.domain.service.SubjectLikedDomainService;
import com.polaris.subject.infra.basic.entity.SubjectInfo;
import com.polaris.subject.infra.basic.entity.SubjectLabel;
import com.polaris.subject.infra.basic.entity.SubjectLiked;
import com.polaris.subject.infra.basic.entity.SubjectMapping;
import com.polaris.subject.infra.basic.service.SubjectInfoService;
import com.polaris.subject.infra.basic.service.SubjectLikedService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 题目点赞表 领域service实现了
 *
 * @author polaris
 * @since 2024-01-07 23:08:45
 */
@Service
@Slf4j
public class SubjectLikedDomainServiceImpl implements SubjectLikedDomainService {

    @Resource
    private SubjectLikedService subjectLikedService;

    @Resource
    private SubjectInfoService subjectInfoService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private static final String SUBJECT_LIKED_KEY = "subject.liked";

    private static final String SUBJECT_LIKED_COUNT_KEY = "subject.liked.count";

    private static final String SUBJECT_LIKED_DETAIL_KEY = "subject.liked.detail";

    @Override
    public void add(SubjectLikedBO subjectLikedBO) {
        Long subjectId = subjectLikedBO.getSubjectId();
        String likeUserId = subjectLikedBO.getLikeUserId();
        Integer status = subjectLikedBO.getStatus();
//        String hashKey = buildSubjectLikedKey(subjectId.toString(), likeUserId);
//        redisUtil.putHash(SUBJECT_LIKED_KEY, hashKey, status);

        SubjectLikedMessage subjectLikedMessage = new SubjectLikedMessage();
        subjectLikedMessage.setSubjectId(subjectId);
        subjectLikedMessage.setLikeUserId(likeUserId);
        subjectLikedMessage.setStatus(status);
        rocketMQTemplate.convertAndSend("subject-liked", JSON.toJSONString(subjectLikedMessage));



        String detailKey = SUBJECT_LIKED_DETAIL_KEY + "." + subjectId + "." + likeUserId;
        String countKey = SUBJECT_LIKED_COUNT_KEY + "." + subjectId;
        if (SubjectLikedStatusEnum.LIKED.getCode() == status) {
            redisUtil.increment(countKey, 1);
            redisUtil.set(detailKey, "1");
        } else {
            Integer count = redisUtil.getInt(countKey);
            if (Objects.isNull(count) || count <= 0) {
                return;
            }
            redisUtil.increment(countKey, -1);
            redisUtil.del(detailKey);
        }
    }

    @Override
    public Boolean isLiked(String subjectId, String userId) {
        String detailKey = SUBJECT_LIKED_DETAIL_KEY + "." + subjectId + "." + userId;
        return redisUtil.exist(detailKey);
    }

    @Override
    public Integer getLikedCount(String subjectId) {
        String countKey = SUBJECT_LIKED_COUNT_KEY + "." + subjectId;
        Integer count = redisUtil.getInt(countKey);
        if (Objects.isNull(count) || count <= 0) {
            return 0;
        }
        return redisUtil.getInt(countKey);
    }

    private String buildSubjectLikedKey(String subjectId, String userId) {
        return subjectId + ":" + userId;
    }

    @Override
    public Boolean update(SubjectLikedBO subjectLikedBO) {
        SubjectLiked subjectLiked = SubjectLikedBOConverter.INSTANCE.convertBOToEntity(subjectLikedBO);
        return subjectLikedService.update(subjectLiked) > 0;
    }

    @Override
    public Boolean delete(SubjectLikedBO subjectLikedBO) {
        SubjectLiked subjectLiked = new SubjectLiked();
        subjectLiked.setId(subjectLikedBO.getId());
        subjectLiked.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        return subjectLikedService.update(subjectLiked) > 0;
    }

    @Override
    public void syncLiked() {
        Map<Object, Object> subjectLikedMap = redisUtil.getHashAndDelete(SUBJECT_LIKED_KEY);
        if (log.isInfoEnabled()) {
            log.info("syncLiked.subjectLikedMap:{}", JSON.toJSONString(subjectLikedMap));
        }
        if (MapUtils.isEmpty(subjectLikedMap)) {
            return;
        }
        //批量同步到数据库
        List<SubjectLiked> subjectLikedList = new LinkedList<>();
        subjectLikedMap.forEach((key, val) -> {
            SubjectLiked subjectLiked = new SubjectLiked();
            String[] keyArr = key.toString().split(":");
            String subjectId = keyArr[0];
            String likedUser = keyArr[1];
            subjectLiked.setSubjectId(Long.valueOf(subjectId));
            subjectLiked.setLikeUserId(likedUser);
            subjectLiked.setStatus(Integer.valueOf(val.toString()));
            subjectLiked.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
            subjectLikedList.add(subjectLiked);
        });
        subjectLikedService.batchInsertOrUpdate(subjectLikedList);
    }

    @Override
    public PageResult<SubjectLikedBO> getSubjectLikedPage(SubjectLikedBO subjectLikedBO) {
        PageResult<SubjectLikedBO> pageResult = new PageResult<>();
        pageResult.setPageNo(subjectLikedBO.getPageNo());
        pageResult.setPageSize(subjectLikedBO.getPageSize());
        int start = (subjectLikedBO.getPageNo() - 1) * subjectLikedBO.getPageSize();
        SubjectLiked subjectLiked = SubjectLikedBOConverter.INSTANCE.convertBOToEntity(subjectLikedBO);
        subjectLiked.setLikeUserId(LoginUtil.getLoginId());
        int count = subjectLikedService.countByCondition(subjectLiked);
        if (count == 0) {
            return pageResult;
        }
        List<SubjectLiked> subjectLikedList = subjectLikedService.queryPage(subjectLiked, start,
                subjectLikedBO.getPageSize());
        List<SubjectLikedBO> subjectInfoBOS = SubjectLikedBOConverter.INSTANCE.convertListInfoToBO(subjectLikedList);
        subjectInfoBOS.forEach(info -> {
            SubjectInfo subjectInfo = subjectInfoService.queryById(info.getSubjectId());
            info.setSubjectName(subjectInfo.getSubjectName());
        });
        pageResult.setRecords(subjectInfoBOS);
        pageResult.setTotal(count);
        return pageResult;
    }

    @Override
    public void syncLikedByMsg(SubjectLikedBO subjectLikedBO) {
        List<SubjectLiked> subjectLikedList = new LinkedList<>();
        SubjectLiked subjectLiked = new SubjectLiked();
        subjectLiked.setSubjectId(Long.valueOf(subjectLikedBO.getSubjectId()));
        subjectLiked.setLikeUserId(subjectLikedBO.getLikeUserId());
        subjectLiked.setStatus(subjectLikedBO.getStatus());
        subjectLiked.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        subjectLikedList.add(subjectLiked);
        subjectLikedService.batchInsertOrUpdate(subjectLikedList);
    }

}
