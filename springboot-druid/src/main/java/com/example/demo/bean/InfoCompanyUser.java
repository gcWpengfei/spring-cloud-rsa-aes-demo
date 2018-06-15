package com.example.demo.bean;

//import lombok.Getter;
//import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
//@Getter
//@Setter
public class InfoCompanyUser implements Serializable {

    private String id;


    private String username;


    private Integer status;


    private static final long serialVersionUID = 1L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}