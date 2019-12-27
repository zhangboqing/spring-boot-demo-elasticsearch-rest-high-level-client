package com.zbq.springbootelasticsearch.common.elasticsearch.enums;

/**
 * @author zhangboqing
 * @date 2019/12/12
 */
public enum ESFieldType {
	Text("text"),
	Byte("byte"),
	Short("short"),
	Integer("integer"),
	Long("long"),
	Date("date"),
	Float("float"),
	Double("double"),
	Boolean("boolean"),
	Object("object"),
	Keyword("keyword");


	ESFieldType(String typeName) {
		this.typeName = typeName;
	}

	public String typeName;
}
