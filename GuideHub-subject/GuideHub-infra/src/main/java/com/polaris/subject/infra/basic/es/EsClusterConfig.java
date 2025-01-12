package com.polaris.subject.infra.basic.es;

import lombok.Data;

import java.io.Serializable;

/**
 * es集群类
 *
 * @author Polaris
 * @date 2023/12/17
 */
@Data
public class EsClusterConfig implements Serializable {

    /**
     * 集群名称
     */
    private String name;

    /**
     * 集群节点
     */
    private String nodes;

}
