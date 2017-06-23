$(function() {
	$(selall).on("click", function() {
		$("input[id^=eid-]").each(function() {
			$(this).prop("checked", $(selall).prop("checked"));
		});
	});
	$(deleteBtn).on("click", function() {
		ids = "";
		$("input[id^=eid-]").each(function() {
			if ($(this).prop("checked")) {
				ids += $(this).val() + ",";
			}
		});
		if (ids == "") {
			operateAlert(false, "", "您还未选择要删除的数据，请先选中");
		} else {
			if (window.confirm("您确认要删除这些雇员信息吗？")) {
				window.location = "pages/back/admin/emp/delete.action?ids="
						+ ids;
			}
		}
	});

})