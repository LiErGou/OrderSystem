package com.amaker.wlo;

public class DishMenu {
	private int id;
	private int price;
	private int typeId;
	private String name;
	private String pic;
	private String remark;
	private String discribe;
	private int grade;
	
	public DishMenu(int id,int price,int typeId,String name,String pic,String remark, String discribe,int grade){
		this.id=id;
		this.price=price;
		this.typeId=typeId;
		this.name=name;
		this.pic=pic;
		this.remark=remark;
		this.discribe=discribe;
		this.grade=grade;
	}
	public DishMenu() {
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getDiscribe() {
		return discribe;
	}
	public void setDiscribe(String discribe) {
		this.discribe = discribe;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
}