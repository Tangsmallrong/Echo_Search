package com.thr.project.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 数据源接口(新接入数据源必须实现)
 * @param <T>
 */
public interface DataSource<T> {

    /**
     * 搜索
     *
     * @param searchText 关键词
     * @param pageNum 页数
     * @param pageSize 页面大小
     * @return 返回 page 对象
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);


}
