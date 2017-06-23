package org.travel.dao;

import java.util.List;

import org.travel.util.dao.IBaseDAO;
import org.travel.vo.Emp;

public interface IEmpDAO extends IBaseDAO<String, Emp> {
	public List<Emp> findAllManagers();
	
	public boolean doUpdateLevel(Emp vo);
	
	public List<Emp> findAllByIds(Object[] ids);
	
	public boolean doUpdateLocked(Emp vo);
}
