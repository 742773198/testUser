package com.zlt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Pager<T> {

    private Integer pageNow;

    private Integer pageSize;

    private Long totalCount;

    private Integer pageCount;

    private List<T> data;
}
