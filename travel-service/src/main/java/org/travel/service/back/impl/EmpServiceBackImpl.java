package org.travel.service.back.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.travel.dao.IActionDAO;
import org.travel.dao.IDeptDAO;
import org.travel.dao.IEmpDAO;
import org.travel.dao.ILevelDAO;
import org.travel.dao.IRoleDAO;
import org.travel.exception.ManagerExistedException;
import org.travel.service.back.IEmpServiceBack;
import org.travel.vo.Dept;
import org.travel.vo.Emp;

@Service
public class EmpServiceBackImpl implements IEmpServiceBack {
	@Resource
	private IEmpDAO empDAO;
	@Resource
	private IDeptDAO deptDAO;
	@Resource
	private IRoleDAO roleDAO;
	@Resource
	private IActionDAO actionDAO;
	@Resource
	private ILevelDAO levelDAO;

	@Override
	public Map<String, Object> get(String eid, String password) {
		Map<String, Object> map = new HashMap<String, Object>();
		Emp emp = this.empDAO.findById(eid);
		if (emp != null) {
			if (password.equals(emp.getPassword())) {
				map.put("level", this.levelDAO.findById(emp.getLid()));
			}
		}
		map.put("emp", emp);
		return map;
	}

	@Override
	public Map<String, Set<String>> listRoleAndAction(String eid) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		map.put("allRoles", this.roleDAO.findAllIdByEmp(eid));
		map.put("allActions", this.actionDAO.findAllIdByEmp(eid));
		return map;
	}

	@Override
	public Map<String, Object> getDetails(String eid) {
		Map<String, Object> map = new HashMap<String, Object>();
		Emp emp = this.empDAO.findById(eid);
		if(emp != null) {
			map.put("dept", this.deptDAO.findById(emp.getDid()));
			map.put("level", this.levelDAO.findById(emp.getLid()));
		}
		map.put("emp", emp );
		
		return map;
	}

	@Override
	public Map<String, Object> getAddPre() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("allLevels", this.levelDAO.findAll());
		map.put("allDepts", this.deptDAO.findAll());
		return map;
	}

	@Override
	public boolean add(Emp vo) throws ManagerExistedException {
		if (this.empDAO.findById(vo.getEid()) == null) { //当前eid不存在，可以增加数据
			vo.setHiredate(new Date());
			vo.setLocked(0);
			Emp hr = this.empDAO.findById(vo.getIneid()); //取得当前操作者信息
			if (hr.getDid().equals(2L)) { //判断当前操作者是否是人事部门的
				if ("staff".equals(vo.getLid())) {  //新增雇员为普通职员
					return this.empDAO.doCreate(vo);
				}
				if ("manager".equals(vo.getLid())) {  //新增雇员为经理
					if ("manager".equals(hr.getLid())) {  //操作者是人事部经理
						Dept dept = this.deptDAO.findById(vo.getDid()); //新增雇员所在部门
						if (dept.getEid() == null) {  //该部门没有经理
							if (this.empDAO.doCreate(vo)) { //新增雇员并设置部门经理
								dept.setEid(vo.getEid());
								return this.deptDAO.doUpdateManager(dept);
							}
						} else {
							throw new ManagerExistedException("该部门已经存在经理，无法进行新经理的任命！");
						}
					}
				
				}
			}
		
		}
		return false;
	}

}
