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
import org.travel.vo.Emp;

@Service
public class DeptServiceBackImpl extends AbstractService implements IDeptServiceBack {
	@Resource
	private IDeptDAO deptDAO;
	@Resource
	private IEmpDAO empDAO;

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

	@Override
	public boolean editMgr(Long did, String ineid) {
		Dept dept = this.deptDAO.findById(did);
		if (dept != null) {
			Emp emp = this.empDAO.findById(ineid); // 查询当前操作者的信息
			if ("manager".equals(emp.getLid())) { // 当前操作者是经理级别（人事部经理），有修改部门经理的权限
				Emp vo = new Emp();
				vo.setEid(dept.getEid());// 部门原经理的信息
				dept.setEid(null);
				if (this.deptDAO.doUpdateManager(dept)) { // 部门的经理已删除
					vo.setLid("staff");
					vo.setIneid(ineid);
					return this.empDAO.doUpdateLevel(vo); // 修改原经理的级别manager --> staff
				}

			}

		}
		
//		Emp emp = this.empDAO.findById(ineid);
//		if ("manager".equals(emp.getLid())) {
//			Dept dept = this.deptDAO.findById(did);
//			if (dept != null) {
//				Emp vo = new Emp();
//				vo.setEid(dept.getEid());
//				dept.setEid(null);
//				if (this.deptDAO.doUpdateManager(dept)) {
//					vo.setLid("staff");
//					vo.setIneid(ineid);
//					return this.empDAO.doUpdateLevel(vo);
//				}
//			}
//		}

		return false;
	}

}
