package com.didi.little;

public class UserModel {
	private String userface;
	private String uesrname;
	private String usernumber;
	private String firstLetter;

	public String getUserface() {
		return userface;
	}

	public void setUserface(String userface) {
		this.userface = userface;
	}

	public String getUesrname() {
		return uesrname;
	}

	public void setUesrname(String uesrname) {
		this.uesrname = uesrname;
	}

	public void setUsernumber(String usernumber){
		this.usernumber=usernumber;
	}
	public String getUsernumber(){
		return  usernumber;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}

}
