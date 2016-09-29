package com.vo;

import java.util.ArrayList;
import java.util.List;

public class AdminInfoVo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String adminId;
	private String adminName;
	private List<String> allActionKey = new ArrayList<String>();// 所有权限
	private List<String> adminActionKey = new ArrayList<String>();// 用户权限

	private String departmentId;// 所属部门ID
	private String xzbId;// 所属乡镇办ID
	private boolean chief = false;// 是否为负责人

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public List<String> getAllActionKey() {
		return allActionKey;
	}

	public void setAllActionKey(List<String> allActionKey) {
		this.allActionKey = allActionKey;
	}

	public List<String> getAdminActionKey() {
		return adminActionKey;
	}

	public void setAdminActionKey(List<String> adminActionKey) {
		this.adminActionKey = adminActionKey;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public boolean isChief() {
		return chief;
	}

	public void setChief(boolean chief) {
		this.chief = chief;
	}

	public String getXzbId() {
		return xzbId;
	}

	public void setXzbId(String xzbId) {
		this.xzbId = xzbId;
	}

}
