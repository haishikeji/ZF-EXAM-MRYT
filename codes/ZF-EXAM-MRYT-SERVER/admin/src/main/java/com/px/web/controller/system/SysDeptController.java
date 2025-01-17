package com.px.web.controller.system;

import java.util.List;

import com.px.common.annotation.RequestLog;

import com.px.common.core.page.TableDataInfo;
import com.px.common.pojo.request.DeleteRequest;

import com.px.common.utils.poi.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.px.common.annotation.Log;
import com.px.common.constant.UserConstants;
import com.px.common.core.controller.BaseController;
import com.px.common.core.domain.AjaxResult;
import com.px.common.core.domain.entity.SysDept;
import com.px.common.enums.BusinessType;
import com.px.common.utils.StringUtils;
import com.px.system.service.ISysDeptService;

import javax.servlet.http.HttpServletResponse;

/**
 * 部门信息
 * 
 * @author 品讯科技
 */
@RestController
@RequestMapping("/system/dept")
@Slf4j
public class SysDeptController extends BaseController
{
    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @ApiOperation("获取单位列表")
    @RequestLog
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDept dept)
    {
        startPage();
        List<SysDept> depts = deptService.selectDeptList(dept);
        return getDataTable(depts);
    }

//    @ApiOperation("获取全部单位列表")
//    @RequestLog
//    @GetMapping("/listAll")
//    public AjaxResult listAll(SysDept dept)
//    {
//        List<SysDept> depts = deptService.selectDeptList(dept);
//        return AjaxResult.success(depts);
//    }




    /**
     * 查询部门列表（排除节点）
     */
    @ApiOperation("获取单位列表（排除节点）")
    @RequestLog
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public AjaxResult excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @ApiOperation("根据单位编号获取详细信息")
    @RequestLog
//    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable Long deptId)
    {
        deptService.checkDeptDataScope(deptId);
        return success(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     */
    @ApiOperation("新增单位")
    @RequestLog
//    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody SysDept dept)
    {
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("新增单位'" + dept.getDeptName() + "'失败，单位名称已存在");
        }
        dept.setCreateBy(getUsername());
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @ApiOperation("修改单位")
    @RequestLog
//    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "单位管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@Validated @RequestBody SysDept dept)
    {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("修改单位'" + dept.getDeptName() + "'失败，单位名称已存在");
        }
        else if (dept.getParentId().equals(deptId))
        {
            return error("修改单位'" + dept.getDeptName() + "'失败，上级单位不能是自己");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0)
        {
            return error("该单位包含未停用的子单位！");
        }
        dept.setUpdateBy(getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @ApiOperation("删除单位")
    @RequestLog
//    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "单位管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    public AjaxResult remove(@RequestBody DeleteRequest reId)
    {
        Long deptId = reId.getId();
        if (deptService.hasChildByDeptId(deptId))
        {
            return warn("存在下级单位,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return warn("单位存在用户,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        return toAjax(deptService.deleteDeptById(deptId));
    }
    @ApiOperation("导出单位")
    @Log(title = "单位管理", businessType = BusinessType.EXPORT)
//    @PreAuthorize("@ss.hasPermi('system:dept:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response,SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        ExcelUtil<SysDept> util = new ExcelUtil<SysDept>(SysDept.class);
        util.exportExcel(response, depts, "单位管理数据");
//        AjaxResult result= util.exportExcel(depts, "单位管理数据");
//        String fileName = (String) result.get("msg");
//        try {
//            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
//            String filePath = RuoYiConfig.getDownloadPath() + fileName;
//
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
//            FileUtils.setAttachmentResponseHeader(response, realFileName);
//            FileUtils.writeBytes(filePath, response.getOutputStream());
//            FileUtils.deleteFile(filePath);
//        }catch (Exception e){
//            log.error("下载文件发生异常：",e);
//        }

    }


}
