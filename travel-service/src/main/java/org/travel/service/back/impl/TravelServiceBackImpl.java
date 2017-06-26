package org.travel.service.back.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.travel.dao.IItemDAO;
import org.travel.dao.ITravelDAO;
import org.travel.service.back.ITravelServiceBack;
import org.travel.service.back.abs.AbstractService;
import org.travel.vo.Travel;

@Service
public class TravelServiceBackImpl extends AbstractService implements ITravelServiceBack {
	@Resource
	private IItemDAO itemDAO;
	@Resource
	private ITravelDAO travelDAO;

	@Override
	public Map<String, Object> addPre() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("allItems", this.itemDAO.findAll());
		return map;
	}

	@Override
	public boolean add(Travel vo) {
		if (vo.getSdate().before(vo.getEdate())) {  //出差的开始日期应该在结束日期之前
			vo.setAudit(9);	//audit=9,表示该申请未提交
			return this.travelDAO.doCreate(vo);
		}
		return false ;
	}

}
