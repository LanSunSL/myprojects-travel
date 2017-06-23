package org.travel.dao;

import java.util.Set;

import org.travel.util.dao.IBaseDAO;
import org.travel.vo.Role;

public interface IRoleDAO extends IBaseDAO<String, Role> {
	/**
	 * 根据雇员编号查询所有的角色编号（角色id）
	 * @param eid 雇员id
	 * @return 所有的角色标记信息
	 */
	public Set<String> findAllIdByEmp(String eid) ;
}
