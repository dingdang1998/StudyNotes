package org.lanqiao.entity;

public class Student {
	private int id ;
	private String stuName ;
	private int stuAge ;
	private String graName ;
	private boolean stuSex ;
	
	public Student() {
	}
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}


	public Student(int id, String stuName, int stuAge, String graName) {
		this.id = id;
		this.stuName = stuName;
		this.stuAge = stuAge;
		this.graName = graName;
	}

	public Student(int id, String stuName, int stuAge, String graName, boolean stuSex) {
		super();
		this.id = id;
		this.stuName = stuName;
		this.stuAge = stuAge;
		this.graName = graName;
		this.stuSex = stuSex;
	}



	public boolean isStuSex() {
		return stuSex;
	}


	public void setStuSex(boolean stuSex) {
		this.stuSex = stuSex;
	}


	public String getStuName() {
		return stuName;
	}
	public void setStuName(String stuName) {
		this.stuName = stuName;
	}
	public int getStuAge() {
		return stuAge;
	}
	public void setStuAge(int stuAge) {
		this.stuAge = stuAge;
	}
	public String getGraName() {
		return graName;
	}
	public void setGraName(String graName) {
		this.graName = graName;
	} 
	
	@Override
	public String toString() {
		return id+"-"+this.stuName+"-"+this.stuAge+"-"+this.graName +"-性别:"+this.stuSex ;
	}
	
}
