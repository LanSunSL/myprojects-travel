package org.travel.service.back;

import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.travel.vo.Dept;

public interface IDeptServiceBack {
	@RequiresRoles(value= {"emp","empshow"}, logical=Logical.OR)
	@RequiresPermissions(value={"dept:list","deptshow:list"}, logical=Logical.OR)
	public Map<String, Object> list();

	@RequiresRoles(value = { "emp" })
	@RequiresPermissions(value = { "dept:edit" })
	public boolean edit(Dept vo);
}
