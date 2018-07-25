package net.lynxlee.toolsstudio.sqlite.dbobj;

//import java.math.BigDecimal;
//import java.util.Calendar;

/**
 * sqllite表字段对应类
 * 
 * @author air
 * 
 */
public class Field {

	/**
	 * 字段名称
	 */
	private String FieldName;
	// private String FieldType;
	/**
	 * 字段内容
	 */
	private Object FieldValue;
	/**
	 * 是否主键
	 */
	private boolean isKey;

	/**
	 * 字段类构造器
	 * @param fieldName 字段名称
	 * @param fieldValue 字段内容
	 * @param isKey 是否主键
	 */
	public Field(String fieldName, Object fieldValue, boolean isKey) {
		this.FieldName = fieldName;
		this.FieldValue = fieldValue;
		this.isKey = isKey;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public String getFieldName() {
		return FieldName;
	}

	public void setFieldName(String fieldName) {
		FieldName = fieldName;
	}

	// public String getFieldType() {
	// return FieldType;
	// }

	// public void setFieldType(String fieldType) {
	// this.FieldType = fieldType;
	// if (null != fieldType) {
	// if ("INTEGER".equalsIgnoreCase(fieldType)) {
	// FieldValue = new Integer(0);
	// } else if ("BOOL".equalsIgnoreCase(fieldType)) {
	// FieldValue = new Boolean(false);
	// } else if ("DOUBLE".equalsIgnoreCase(fieldType)) {
	// FieldValue = new Double(0);
	// } else if ("FLOAT".equalsIgnoreCase(fieldType)) {
	// FieldValue = new Float(0);
	// } else if ("REAL".equalsIgnoreCase(fieldType)) {
	// FieldValue = new Float(0);
	// } else if ("CHAR".equalsIgnoreCase(fieldType)) {
	// FieldValue = new Character((char) 0);
	// } else if ("TEXT".equalsIgnoreCase(fieldType)) {
	// FieldValue = new String();
	// } else if ("VARCHAR".equalsIgnoreCase(fieldType)) {
	// FieldValue = new String();
	// } else if ("NUMERIC".equalsIgnoreCase(fieldType)) {
	// FieldValue = new BigDecimal((char) 0);
	// } else if ("DATETIME".equalsIgnoreCase(fieldType)) {
	// FieldValue = Calendar.getInstance();
	// }
	// } else {
	//
	// }
	//
	// }

	public Object getFieldValue() {
		return FieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.FieldValue = fieldValue;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(null != this.getFieldName() ? "FieldName:"
				+ this.getFieldName() + " " : "");
		// sb.append(null != this.getFieldType() ? "FieldType:"
		// + this.getFieldType() + " " : "");
		sb.append(null != this.getFieldValue() ? "FieldValue:"
				+ this.getFieldValue().toString() + " " : "");
		sb.append(true == this.isKey() ? "isKey:" + this.isKey() + " " : "");
		return sb.toString();
	}

}
