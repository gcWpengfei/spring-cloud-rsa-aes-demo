package com.wpf.util;

public class Req {
	public  String data;
	public  String encryptkey;

	public Req(String data, String encryptkey) {
		super();
		this.data = data;
		this.encryptkey = encryptkey;
	}
	
	public Req() {
		super();
	
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getEncryptkey() {
		return encryptkey;
	}

	public void setEncryptkey(String encryptkey) {
		this.encryptkey = encryptkey;
	}

	@Override
	public String toString() {
		return "data:" + data + "\nencryptkey:" + encryptkey;
	}
}