package org.travel.action.back;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.travel.exception.ManagerExistedException;
import org.travel.service.back.IEmpServiceBack;
import org.travel.util.action.abs.AbstractBaseAction;
import org.travel.util.encrypt.PasswordUtil;
import org.travel.util.web.FileUtils;
import org.travel.vo.Emp;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/pages/back/admin/emp/*")
public class EmpActionBack extends AbstractBaseAction {
	private static final String FLAG = "雇员";

	@Resource
	private IEmpServiceBack empServiceBack;

	@RequestMapping("add_pre")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:add")
	public ModelAndView addPre() {
		ModelAndView mav = new ModelAndView(super.getUrl("emp.add.page"));
		mav.addAllObjects(this.empServiceBack.getAddPre());
		return mav;
	}
	
	@RequestMapping("add_check")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:add")
	public ModelAndView check(HttpServletResponse response, String eid) {
		super.print(response, this.empServiceBack.getEid(eid) == null);
		return null;
	}

	@RequestMapping("add")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:add")
	public ModelAndView add(Emp vo, MultipartFile pic, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		vo.setIneid(super.getEid());
		vo.setPassword(PasswordUtil.getPassword(vo.getPassword()));
		FileUtils fileUtil = null;
		if (!pic.isEmpty()) {
			fileUtil = new FileUtils(pic);
			vo.setPhoto(fileUtil.createFileName());
		}
		try {
			if (this.empServiceBack.add(vo)) {
				if (fileUtil != null) {
					fileUtil.saveFile(request, "upload/member", vo.getPhoto());
				}
				super.setUrlAndMsg(request, "emp.add.action", "vo.add.success", FLAG);
			} else {
				super.setUrlAndMsg(request, "emp.add.action", "vo.add.failure", FLAG);
			}
		} catch (ManagerExistedException e) {
			super.setUrlAndMsg(request, "emp.add.action", "mgr.exist.msg");
		}

		return mav;
	}

	@RequestMapping("edit_pre")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:edit")
	public ModelAndView editPre(String eid) {
		ModelAndView mav = new ModelAndView(super.getUrl("emp.edit.page"));
		return mav;
	}

	@RequestMapping("edit")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:edit")
	public ModelAndView edit(Emp vo, MultipartFile pic, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		// super.setUrlAndMsg(request, "emp.list.action", "vo.edit.failure",
		// FLAG);
		super.setUrlAndMsg(request, "emp.list.action", "vo.edit.success", FLAG);
		return mav;
	}

	@RequestMapping("get")
	@RequiresUser
	@RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
	@RequiresPermissions(value = { "emp:get", "empshow:get" }, logical = Logical.OR)
	public ModelAndView get(String eid, HttpServletResponse response) {
		Map<String, Object> map = this.empServiceBack.getDetails(eid);
		JSONObject obj = new JSONObject();
		obj.put("emp", map.get("emp"));
		obj.put("dept", map.get("dept"));
		obj.put("level", map.get("level"));
		super.print(response, obj);
		return null;
	}

	@RequestMapping("list")
	@RequiresUser
	@RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
	@RequiresPermissions(value = { "emp:list", "empshow:list" }, logical = Logical.OR)
	public ModelAndView list(String ids, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("emp.list.page"));
		return mav;
	}

	@RequestMapping("delete")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:delete")
	public ModelAndView delete(String ids, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		// super.setUrlAndMsg(request, "emp.list.action", "vo.delete.failure",
		// FLAG);
		super.setUrlAndMsg(request, "emp.list.action", "vo.delete.success", FLAG);
		return mav;
	}
}
