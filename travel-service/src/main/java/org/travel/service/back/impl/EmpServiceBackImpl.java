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
import org.travel.exception.LevelNotEnoughException;
import org.travel.exception.ManagerExistedException;
import org.travel.service.back.IEmpServiceBack;
import org.travel.service.back.abs.AbstractService;
import org.travel.vo.Dept;
import org.travel.vo.Emp;

@Service
public class EmpServiceBackImpl extends AbstractService implements IEmpServiceBack {
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

	@Override
	public Emp getEid(String eid) {
		return this.empDAO.findById(eid);
	}

	@Override
	public Map<String, Object> list(long currentPage, int lineSize, String column, String keyWord) {
		Map<String, Object> map = new HashMap<String, Object> ();
		Map<String, Object> param = super.handleParam(currentPage, lineSize, column, keyWord);
		map.put("allEmps", this.empDAO.findAllSplit(param));
		map.put("allRecorders", this.empDAO.getAllCount(param));
		map.put("allDepts", this.deptDAO.findAll());
		map.put("allLevels", this.levelDAO.findAll());
		return map;
	}

	@Override
	public Map<String, Object> getEditPre(String eid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("allLevels", this.levelDAO.findAll());
		map.put("allDepts", this.deptDAO.findAll());
		map.put("emp", this.empDAO.findById(eid));
		return map;
	}

	@Override
	public boolean edit(Emp vo) throws ManagerExistedException, LevelNotEnoughException {
		Emp hr = this.empDAO.findById(vo.getIneid());  //当前修改者信息
		Emp oldEmp = this.empDAO.findById(vo.getEid()); //要修改员工的原始信息
		Dept oldDept = this.deptDAO.findById(oldEmp.getDid());
		Dept newDept = this.deptDAO.findById(vo.getDid());

		if ("manager".equals(oldEmp.getLid()) || "manager".equals(vo.getLid())) {  //要修改经理的信息，必须是人事manager
			if ("staff".equals(hr.getLid())){
				throw new LevelNotEnoughException("您不具备当前操作权限！");
			}
			if ("manager".equals(hr.getLid())) {
				if (oldEmp.getLid().equals(vo.getLid())) {  //修改前后都是经理
					if (!oldEmp.getDid().equals(vo.getDid())) { //改变了部门
						if (newDept.getEid() != null) {  //新部门已有经理
							throw new ManagerExistedException("该部门已经存在经理，无法进行新经理的任命！");
						} else {
							oldDept.setEid(null);
							this.deptDAO.doUpdateManager(oldDept); //原部门领导降级
							newDept.setEid(vo.getEid()); 
							this.deptDAO.doUpdateManager(newDept);  //新部门增加经理
						}
					}
				} else if ("manager".equals(oldEmp.getLid())) {
					oldDept.setEid(null);
					this.deptDAO.doUpdateManager(oldDept); //原部门领导降级
				} else {
					newDept.setEid(vo.getEid()); 
					this.deptDAO.doUpdateManager(newDept);  //新部门增加经理
				}
			}
		}
		return this.empDAO.doUpdate(vo);
		
	}

}
