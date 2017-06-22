package org.travel.service.back;

import java.util.Map;
import java.util.Set;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.travel.exception.ManagerExistedException;
import org.travel.vo.Emp;

public interface IEmpServiceBack {
	/**
	 * 根据雇员id获得雇员完整信息
	 * @param eid 雇员编号
	 * @param password 雇员密码
	 * @return 包括如下内容：<br>
	 * 1、key = emp、value = 雇员对象，如果该雇员不存在返回null<br>
	 * 2、如果雇员信息可以查询成功，则查询对应级别信息。key = level
	 */
	public Map<String,Object> get(String eid,String password) ;
	/**
	 * 根据雇员编号取得雇员对应的角色与权限数据信息，该操作主要执行如下功能：<br>
	 * 1、调用IRoleDAO.findAllIdByEmp()查询出所有的角色标记信息；<br>
	 * 2、调用IActionDAO.findAllIdByEmp()查询出所有的权限标记信息；<br>
	 * @param eid 要查询角色和权限的雇员编号
	 * @return 返回Map集合数据包含有如下内容：<br>
	 * 1、key = allRoles、value = 所有的角色标记信息；<br>
	 * 2、key = allActions、value = 所有的权限标记信息；<br>
	 */
	public Map<String,Set<String>> listRoleAndAction(String eid) ;
	
	/**
	 * 根据雇员id获得雇员完整信息
	 * @param eid 雇员编号
	 * @return 包括如下内容：<br>
	 * 1、key = emp、value = 雇员对象，如果该雇员不存在返回null<br>
	 * 2、key = dept, value = 部门的详细信息<br>
	 * 3、key = level, value = 雇员级别信息<br> 
	 */
	@RequiresRoles(value={"emp","empshow"}, logical=Logical.OR)
	@RequiresPermissions(value={"emp:get","empshow:get"}, logical=Logical.OR)
	public Map<String,Object> getDetails(String eid) ;
	
	@RequiresRoles(value={"emp"})
	@RequiresPermissions(value={"emp:add"})
	public Map<String,Object> getAddPre() ;
	
	@RequiresRoles(value={"emp"})
	@RequiresPermissions(value={"emp:add"})
	public boolean add(Emp vo) throws ManagerExistedException;
	
	@RequiresRoles(value={"emp"})
	@RequiresPermissions(value={"emp:add"})
	public Emp getEid(String eid) ;
}
