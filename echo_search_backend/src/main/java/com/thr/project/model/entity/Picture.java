package com.thr.project.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片
 */
@Data
public class Picture implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String url;
}