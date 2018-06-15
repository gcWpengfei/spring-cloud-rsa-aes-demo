package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.bean.InfoCompanyUser;

import java.util.List;

@Mapper
public interface InfoCompanyUserMapper {
    
	int deleteByPrimaryKey(String id);
	
    void updateBatch(List<InfoCompanyUser> list);

}