package org.travel.dao;

import java.util.List;

import org.travel.util.dao.IBaseDAO;
import org.travel.vo.Emp;

public interface IEmpDAO extends IBaseDAO<String, Emp> {
	public List<Emp> findAllManagers();
}
