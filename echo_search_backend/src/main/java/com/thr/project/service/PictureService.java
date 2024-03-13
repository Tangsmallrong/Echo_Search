package com.thr.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thr.project.model.dto.post.PostQueryRequest;
import com.thr.project.model.entity.Picture;
import com.thr.project.model.entity.Post;
import com.thr.project.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图片服务
 *
 * @author <a href="https://github.com/Tangsmallrong">thr</a>
 *
 */
public interface PictureService {
    /**
     * 通过标题搜索词查询图片
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);
}
