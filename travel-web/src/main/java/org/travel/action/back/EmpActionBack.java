package org.travel.action.back;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.travel.exception.LevelNotEnoughException;
import org.travel.exception.ManagerExistedException;
import org.travel.service.back.IEmpServiceBack;
import org.travel.util.action.abs.AbstractBaseAction;
import org.travel.util.encrypt.PasswordUtil;
import org.travel.util.split.ActionSplitPageUtil;
import org.travel.util.web.FileUtils;
import org.travel.vo.Dept;
import org.travel.vo.Emp;
import org.travel.vo.Level;

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
		mav.addAllObjects(this.empServiceBack.getEditPre(eid));
		return mav;
	}

	@RequestMapping("edit")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:edit")
	public ModelAndView edit(Emp vo, MultipartFile pic, HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		vo.setIneid(super.getEid());
		if (vo.getPassword() == null || "".equals(vo.getPassword())) {
			vo.setPassword(null);
		} else {
			vo.setPassword(PasswordUtil.getPassword(vo.getPassword()));
		}
		FileUtils fileUtil = null;
		if (!pic.isEmpty()) {
			fileUtil = new FileUtils(pic);
			if ("nophoto.png".equals(vo.getPhoto())) {  //之前没有图片，使用的是默认nophoto.png
				vo.setPhoto(fileUtil.createFileName());
			}
		}
		
		try {
			if (this.empServiceBack.edit(vo)) {
				if (fileUtil != null) {
					fileUtil.saveFile(request, "upload/member", vo.getPhoto());
				}
				super.setUrlAndMsg(request, "emp.list.action", "vo.edit.success", FLAG);
			} else {
				super.setUrlAndMsg(request, "emp.list.action", "vo.edit.failure", FLAG);
			}
		} catch (ManagerExistedException e) {
			super.setUrlAndMsg(request, "emp.list.action", "mgr.exist.msg");
		} catch (LevelNotEnoughException e) {
			super.setUrlAndMsg(request, "emp.list.action", "level.not.enough.msg");
		}
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
		ActionSplitPageUtil aspu = new ActionSplitPageUtil(request,"雇员编号:eid|雇员姓名:ename|联系电话:phone", super.getMsg("emp.list.action"));
		Map<String, Object> map = this.empServiceBack.list(aspu.getCurrentPage(), aspu.getLineSize(), aspu.getColumn(), aspu.getKeyWord());
		mav.addAllObjects(map);
		
		List<Dept> allDepts = (List<Dept>) map.get("allDepts");
		Map<Long, String> deptMap = new HashMap<Long, String>();
		Iterator<Dept> deptIter = allDepts.iterator();
		while (deptIter.hasNext()) {
			Dept dept = deptIter.next();
			deptMap.put(dept.getDid(), dept.getDname());
		}
		mav.addObject("allDepts", deptMap);
		
		List<Level> allLevels = (List<Level>) map.get("allLevels");
		Map<String, String> levelMap = new HashMap<String, String>();
		Iterator<Level> levelIter = allLevels.iterator();
		while (levelIter.hasNext()) {
			Level level = levelIter.next();
			levelMap.put(level.getLid(), level.getTitle());
		}
		mav.addObject("allLevels", levelMap);
		
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
