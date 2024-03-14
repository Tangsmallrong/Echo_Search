package com.thr.project.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thr.project.common.ErrorCode;
import com.thr.project.datasource.*;
import com.thr.project.exception.BusinessException;
import com.thr.project.exception.ThrowUtils;
import com.thr.project.model.dto.post.PostQueryRequest;
import com.thr.project.model.dto.search.SearchRequest;
import com.thr.project.model.dto.user.UserQueryRequest;
import com.thr.project.model.entity.Picture;
import com.thr.project.model.enums.SearchTypeEnum;
import com.thr.project.model.vo.PostVO;
import com.thr.project.model.vo.SearchVO;
import com.thr.project.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 *
 * @author thr
 */
@Component
@Slf4j
public class SearchFacade {
    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String type = searchRequest.getType();
        // 获取搜索关键词
        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        // 如果类型为空, 则搜索所有的数据
        if (StringUtils.isBlank(type)) {
            // 原生多线程
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                // 查用户
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
                return userVOPage;
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                // 查帖子
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postDataSource.doSearch(searchText, current, pageSize);
                return postVOPage;
            });

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                // 查图片
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, current, pageSize);
                return picturePage;
            });

            // 等待三个查询都结束, 才执行下一步
            CompletableFuture.allOf(userTask, postTask, pictureTask).join();

            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();

                SearchVO searchVO = new SearchVO();
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
            ThrowUtils.throwIf(searchTypeEnum == null, ErrorCode.PARAMS_ERROR);
            SearchVO searchVO = new SearchVO();

            // 根据传入的类型来定义 dataSource 的类型
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);

            // 如果类型不为空且非法, 抛出请求参数异常
            ThrowUtils.throwIf(dataSource == null, ErrorCode.PARAMS_ERROR);

            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }
}
