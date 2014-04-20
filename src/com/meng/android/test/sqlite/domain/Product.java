package com.meng.android.test.sqlite.domain;

import java.math.BigDecimal;

import com.j256.ormlite.field.DatabaseField;

public class Product {

	public static final String F_CODE = "code";
	public static final String F_DESCRIPTION = "description";
	public static final String F_PRICE = "price";
	public static final String F_STOCK = "stock";
	public static final String F_FILE = "file";

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField(unique = true, canBeNull = false, columnName = F_CODE)
	private String code;
	@DatabaseField(canBeNull = false)
	private String description;
	@DatabaseField
	private BigDecimal price = BigDecimal.ZERO;
	@DatabaseField
	private Double stock = 0d;
	@DatabaseField
	private String file;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	// transient
	private boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void toogleSelected() {
		this.selected = !this.selected;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + (selected ? 1231 : 1237);
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (selected != other.selected)
			return false;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Product [id=");
		builder.append(id);
		builder.append(", code=");
		builder.append(code);
		builder.append(", description=");
		builder.append(description);
		builder.append(", price=");
		builder.append(price);
		builder.append(", stock=");
		builder.append(stock);
		builder.append(", file=");
		builder.append(file);
		builder.append(", selected=");
		builder.append(selected);
		builder.append("]");
		return builder.toString();
	}

}