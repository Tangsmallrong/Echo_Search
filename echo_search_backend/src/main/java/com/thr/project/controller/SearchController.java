package com.thr.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thr.project.common.BaseResponse;
import com.thr.project.common.ErrorCode;
import com.thr.project.common.ResultUtils;
import com.thr.project.exception.BusinessException;
import com.thr.project.exception.ThrowUtils;
import com.thr.project.manager.SearchFacade;
import com.thr.project.model.dto.post.PostQueryRequest;
import com.thr.project.model.dto.search.SearchRequest;
import com.thr.project.model.dto.user.UserQueryRequest;
import com.thr.project.model.entity.Picture;
import com.thr.project.model.enums.SearchTypeEnum;
import com.thr.project.model.vo.PostVO;
import com.thr.project.model.vo.SearchVO;
import com.thr.project.model.vo.UserVO;
import com.thr.project.service.PictureService;
import com.thr.project.service.PostService;
import com.thr.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 聚合查询接口
 *
 * @author <a href="https://github.com/Tangsmallrong">thr</a>
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }
}
