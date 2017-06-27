package org.travel.service.back;

import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.travel.vo.Travel;

public interface ITravelServiceBack {
	@RequiresRoles("travel")
	@RequiresPermissions("travel:add")
	public Map<String, Object> addPre();

	@RequiresRoles("travel")
	@RequiresPermissions("travel:add")
	public boolean add(Travel vo);

	@RequiresRoles("travel")
	@RequiresPermissions("travel:self")
	public Map<String, Object> listSelf(String seid, long currentPage, int lineSize, String column, String keyWord);
}
