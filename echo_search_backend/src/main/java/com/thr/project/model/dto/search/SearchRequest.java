package com.thr.project.model.dto.search;

import com.thr.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/Tangsmallrong">thr</a>
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchRequest extends PageRequest implements Serializable {

    /**
     * 统一搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}