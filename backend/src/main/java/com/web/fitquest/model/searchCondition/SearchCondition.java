package com.web.fitquest.model.searchCondition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchCondition {
	private String tag;
	private String key;
	private String word;
	private String orderByDir;
	private String orderBy;

}
