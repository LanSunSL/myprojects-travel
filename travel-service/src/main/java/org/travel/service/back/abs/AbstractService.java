package org.travel.service.back.abs;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractService {
	
	public Map<String,Object> handleParam(long currentPage, int lineSize, String column, String keyWord) {
		Map<String, Object> paramMap = new HashMap<String,Object>();
		
		paramMap.put("start", (currentPage-1)*lineSize);
		paramMap.put("lineSize", lineSize);
		if ("".equals(keyWord) || "null".equals(keyWord) || keyWord == null) {
			paramMap.put("keyWord", null);
		} else {
			paramMap.put("keyWord", "%"+keyWord+"%");
		}
		if ("".equals(column) || "null".equals(column) || column == null) {
			paramMap.put("column", null);
		} else {
			paramMap.put("column", column);
		}
		return paramMap;
	}

}
