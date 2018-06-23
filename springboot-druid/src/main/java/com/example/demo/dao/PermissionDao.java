package com.example.demo.dao;


import com.example.demo.bean.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PermissionDao {

    List<Permission> getByMap(Map<String, Object> map);

    Permission getById(Integer id);

    Integer create(Permission permission);

    int update(Permission permission);

    List<Permission> getByUserId(Integer userId);

}