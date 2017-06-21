package org.travel.service.back.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.travel.dao.IDeptDAO;
import org.travel.dao.IEmpDAO;
import org.travel.service.back.IDeptServiceBack;
import org.travel.service.back.abs.AbstractService;
import org.travel.vo.Dept;
@Service
public class DeptServiceBackImpl extends AbstractService implements IDeptServiceBack {
	@Resource
	private IDeptDAO deptDAO ;
	@Resource
	private IEmpDAO empDAO ;
	@Override
	public Map<String, Object> list() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("allDepts", this.deptDAO.findAll());
		map.put("allManagers", this.empDAO.findAllManagers());
		return map;
	}
	@Override
	public boolean edit(Dept vo) {
		return this.deptDAO.doUpdate(vo);
	}

}
