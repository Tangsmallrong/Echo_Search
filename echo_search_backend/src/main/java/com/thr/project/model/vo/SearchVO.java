package com.thr.project.model.vo;

import com.thr.project.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索
 *
 * @author <a href="https://github.com/Tangsmallrong">thr</a>
 */
@Data
public class SearchVO implements Serializable {
    private List<UserVO> userList;

    private List<PostVO> postList;

    private List<Picture> pictureList;

    private static final long serialVersionUID = 1L;
}
