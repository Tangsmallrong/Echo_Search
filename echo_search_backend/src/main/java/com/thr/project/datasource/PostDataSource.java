package com.thr.project.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thr.project.model.dto.post.PostQueryRequest;
import com.thr.project.model.vo.PostVO;
import com.thr.project.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 帖子数据源实现
 *
 * @author <a href="https://github.com/Tangsmallrong">thr</a>
 */
@Service
@Slf4j
public class PostDataSource implements DataSource<PostVO> {

    @Resource
    private PostService postService;

    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent(pageNum);
        postQueryRequest.setPageSize(pageSize);

        // todo 参数不适配 需要继续优化
        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, null);
        return postVOPage;
    }

}




