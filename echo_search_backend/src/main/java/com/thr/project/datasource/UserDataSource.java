package com.thr.project.datasource;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thr.project.common.ErrorCode;
import com.thr.project.constant.CommonConstant;
import com.thr.project.exception.BusinessException;
import com.thr.project.exception.ThrowUtils;
import com.thr.project.mapper.UserMapper;
import com.thr.project.model.dto.user.UserQueryRequest;
import com.thr.project.model.entity.User;
import com.thr.project.model.enums.UserRoleEnum;
import com.thr.project.model.vo.LoginUserVO;
import com.thr.project.model.vo.UserVO;
import com.thr.project.service.UserService;
import com.thr.project.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.thr.project.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户数据源实现
 *
 * @author <a href="https://github.com/Tangsmallrong">thr</a>
 */
@Service
@Slf4j
public class UserDataSource implements DataSource<UserVO> {

    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
        // 运用适配器模式, 使参数适配
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);

        // 再将封装后的对象传入 userService 方法
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        return userVOPage;
    }
}
