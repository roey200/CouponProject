package com.rands.couponproject.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="income")
public class Income implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1345050153967725331L;

//	@Id
//	@GeneratedValue
//	@Column(nullable=false, columnDefinition="integer")	
	
	@Id 
//    @GeneratedValue
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")	
	private long id;
	
	private String name;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	private IncomeType description;
	private double amount;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public IncomeType getDescription() {
		return description;
	}
	public void setDescription(IncomeType description) {
		this.description = description;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String toString() {
		{
			return "Income [id=" + id + ", amount=" + amount + ", name=" + name 
					+ ", date=" + date + ", description=" + description
					+ "]";
		}
	}

}
