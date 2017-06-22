package org.travel.action.back;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.travel.service.back.IDeptServiceBack;
import org.travel.util.action.abs.AbstractBaseAction;
import org.travel.vo.Dept;
import org.travel.vo.Emp;

@Controller
@RequestMapping("/pages/back/admin/dept/*")
public class DeptActionBack extends AbstractBaseAction {
	@Resource
	private IDeptServiceBack deptServiceBack;

	@RequestMapping("list")
	@RequiresUser
	@RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
	@RequiresPermissions(value = { "dept:list", "deptshow:list" }, logical = Logical.OR)
	public ModelAndView list() {
		ModelAndView mav = new ModelAndView(super.getUrl("dept.list.page"));
		Map<String, Object> map = this.deptServiceBack.list(); 
		mav.addObject("allDepts", map.get("allDepts"));
		List<Emp> allManagers = (List<Emp>) map.get("allManagers");
		Iterator<Emp> iter = allManagers.iterator();
		Map<String, String> managerMap = new HashMap<String, String>();
		while(iter.hasNext()) {
			Emp mgr = iter.next();
			managerMap.put(mgr.getEid(), mgr.getEname());
		}
		mav.addObject("managerMap", managerMap);
		return mav;
	}

	@RequestMapping("edit")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("dept:edit")
	public ModelAndView edit(HttpServletResponse response, Dept vo) {
		super.print(response, this.deptServiceBack.edit(vo));
		return null;
	}
	
	@RequestMapping("editMgr")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions(value={"dept:edit","emp:edit"})
	public ModelAndView editMgr(HttpServletResponse response, Long did) {
		super.print(response, this.deptServiceBack.editMgr(did, super.getEid()));
		return null;
	}
}
