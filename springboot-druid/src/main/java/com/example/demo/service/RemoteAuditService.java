package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.bean.InfoCompanyUser;
import com.example.demo.dao.InfoCompanyUserMapper;

import java.util.*;

/**
 *
 * @Author: wpf
 * @Date: 17:32 2018/4/24
 * @Description: 
 * @param  * @param null  
 * @return   
 */
@Service
public class RemoteAuditService {

   
    @Autowired
    private InfoCompanyUserMapper infoCompanyUserMapper;
   

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
    public void test(){

        List<InfoCompanyUser> list = new ArrayList<InfoCompanyUser>();

        InfoCompanyUser u = new InfoCompanyUser();
        u.setUsername("68112710000");
        u.setStatus(1);
        list.add(u);

        /*u = new InfoCompanyUser();
        u.setUsername("68112710001");
        u.setStatus(1);
        list.add(u);

        u = new InfoCompanyUser();
        u.setUsername("68112710002");
        u.setStatus(1);
        list.add(u);*/

        infoCompanyUserMapper.updateBatch(list);
        
        //int i = 1/0;
    }
}
