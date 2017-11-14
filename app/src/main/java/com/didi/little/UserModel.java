package com.didi.little;

public class UserModel {
	// 联系人姓名
	private String uesrname;
	// 联系人电话
	private String usernumber;
	// firstletter
	private String firstLetter;

	// 对应set get函数

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
