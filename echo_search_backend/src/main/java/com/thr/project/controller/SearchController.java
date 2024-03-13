package com.thr.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thr.project.common.BaseResponse;
import com.thr.project.common.ErrorCode;
import com.thr.project.common.ResultUtils;
import com.thr.project.exception.BusinessException;
import com.thr.project.model.dto.post.PostQueryRequest;
import com.thr.project.model.dto.search.SearchRequest;
import com.thr.project.model.dto.user.UserQueryRequest;
import com.thr.project.model.entity.Picture;
import com.thr.project.model.vo.PostVO;
import com.thr.project.model.vo.SearchVO;
import com.thr.project.model.vo.UserVO;
import com.thr.project.service.PictureService;
import com.thr.project.service.PostService;
import com.thr.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        // 获取搜索关键词
        String searchText = searchRequest.getSearchText();

        // 原生多线程
        CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
            // 查用户
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserName(searchText);
            Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
            return userVOPage;
        });

        CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
            // 查帖子
            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);
            Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
            return postVOPage;
        });

        CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
            // 查图片
            Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
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
            return ResultUtils.success(searchVO);
        } catch (Exception e) {
            log.error("查询异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
        }
    }
}
