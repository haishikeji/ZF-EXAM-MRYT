<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <!--部门数据-->
      <el-row :gutter="20">
        <!--部门数据-->
        <el-col :span="4" :xs="24">
          <el-form-item size="small" class="head-container">
            <TreeselectCom  v-model="queryParams.deptId" :options="deptOptions" :show-count="true" placeholder="请选择单位" />
          </el-form-item>
        </el-col>

          <el-form-item label="积分类型" prop="title">
            <el-select v-model="queryParams.type" @change="setType" placeholder="请选择">
              <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="用户名" prop="phone">
            <el-input v-model="queryParams.phone" placeholder="请输入手机号码" clearable style="width: 240px"
              @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item v-show="queryParams.type < 3" label="年">
            <el-date-picker v-model="queryParams.year" type="year" :clearable="false" value-format="yyyy" placeholder="选择年">
            </el-date-picker>
          </el-form-item>
          <el-form-item v-show="queryParams.type == 1" label="月" prop="month">
            <el-select v-model="queryParams.month" placeholder="请选择">
              <el-option v-for="item in monthArr" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>

      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">

      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['system:user:export']">导出</el-button> </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <!-- <el-table-column label="id" prop="id" width="50" /> -->
      <el-table-column label="用户名" prop="phone" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="昵称" prop="name" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="单位" prop="dname" :show-overflow-tooltip="true" />
      <el-table-column label="积分" prop="points" width="50" />


      <el-table-column label="错误" align="center" prop="wrongnum" width="150">

      </el-table-column>
      <el-table-column label="时间" align="center" prop="updatetime">

      </el-table-column>
      <el-table-column label="排序" align="center" prop="sorts" width="100">

      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope" v-if="scope.row.roleId !== 1">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['system:role:edit']">修改</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['system:role:remove']">删除</el-button> -->
          <el-button size="mini" type="text" icon="" @click="viewLogPoints(scope.row)"
            v-hasPermi="['system:role:edit']">查看历史</el-button>

        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <!-- <el-form-item label="积分类型" prop="title">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
      </el-form-item> -->
        <el-form-item label="分值" prop="point">
          <el-input-number v-model="form.points" controls-position="right" :min="0" />
        </el-form-item>

        <!-- <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm(1)">增 加</el-button>
        <el-button type="danger" @click="submitForm(-1)">减少</el-button>
      </div>
    </el-dialog>


  </div>
</template>
  
<script>
import { listPoint, reducePoint, addPoint } from "@/api/point";
import { deptTreeSelect } from "@/api/system/user";
import { treeselect as menuTreeselect, roleMenuTreeselect } from "@/api/system/menu";
import TreeselectCom from "@riophae/vue-treeselect";
import { delDept } from "../../api/answer";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
export default {
  name: "Role",
  dicts: ['sys_normal_disable'],
  components: { TreeselectCom },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 角色表格数据
      roleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示弹出层（数据权限）
      openDataScope: false,
      menuExpand: false,
      menuNodeAll: false,
      deptExpand: true,
      deptNodeAll: false,
      // 日期范围
      dateRange: [],
      // 数据范围选项
      dataScopeOptions: [
        {
          value: "1",
          label: "全部数据权限"
        },
        {
          value: "2",
          label: "自定数据权限"
        },
        {
          value: "3",
          label: "本单位数据权限"
        },
        {
          value: "4",
          label: "本单位及以下数据权限"
        },
        {
          value: "5",
          label: "仅本人数据权限"
        }
      ],
      typeOptions: [
        {
          value: "1",
          label: "月积分"
        },
        {
          value: "2",
          label: "年积分"
        },
        {
          value: "3",
          label: "总积分"
        },
      ],
      monthArr: [
        { label: '一月份', value: '01' },
        { label: '二月份', value: '02' },
        { label: '三月份', value: '03' },
        { label: '四月份', value: '04' },
        { label: '五月份', value: '05' },
        { label: '六月份', value: '06' },
        { label: '七月份', value: '07' },
        { label: '八月份', value: '08' },
        { label: '九月份', value: '09' },
        { label: '十月份', value: '10' },
        { label: '十一月份', value: '11' },
        { label: '十二月份', value: '12' },
      ],
      // 菜单列表
      menuOptions: [],
      // 单位列表
      deptOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: '1',
        title: undefined,
        year: '' + new Date().getFullYear(),
        month: (new Date().getMonth() + 1) > 9 ? (new Date().getMonth() + 1) + '' : '0' + (new Date().getMonth() + 1)
      },
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 表单校验
      rules: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" }
        ],
        roleKey: [
          { required: true, message: "权限字符不能为空", trigger: "blur" }
        ],
        roleSort: [
          { required: true, message: "角色顺序不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getDeptTree();
  },
  methods: {
    setType() {
      this.$set(this.queryParams, 'month', '');
      this.$set(this.queryParams, 'year', '');
      this.$set(this.queryParams, 'time', '');
    },
    viewLogPoints(row) {
      let id = row.id;
      this.$router.push('/points/his?id=' + id + '&phone=' + row.phone + '&name=' + row.name)

    },
    /** 查询角色列表 */
    getList() {
      this.loading = true;
      console.log(this.queryParams.year)
      if (this.queryParams.year) {
        this.queryParams.time = this.queryParams.year + (this.queryParams.month || '');
        //this.queryParams.year = null;
      }

      listPoint(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.roleList = response.rows;
        this.total = response.total;
        this.loading = false;
      }
      );
    },
    /** 查询菜单树结构 */
    getMenuTreeselect() {
      menuTreeselect().then(response => {
        this.menuOptions = response.data;
      });
    },
    // 所有菜单节点数据
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      let checkedKeys = this.$refs.menu.getCheckedKeys();
      // 半选中的菜单节点
      let halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys();
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
      return checkedKeys;
    },
    // 所有单位节点数据
    getDeptAllCheckedKeys() {
      // 目前被选中的单位节点
      let checkedKeys = this.$refs.dept.getCheckedKeys();
      // 半选中的单位节点
      let halfCheckedKeys = this.$refs.dept.getHalfCheckedKeys();
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
      return checkedKeys;
    },
    /** 根据角色ID查询菜单树结构 */
    getRoleMenuTreeselect(roleId) {
      return roleMenuTreeselect(roleId).then(response => {
        this.menuOptions = response.menus;
        return response;
      });
    },
    /** 根据角色ID查询单位树结构 */
    getDeptTree(roleId) {
      return deptTreeSelect(roleId).then(response => {
        this.deptOptions = response.data;
        return response;
      });
    },
    // 角色状态修改
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.roleName + '"角色吗？').then(function () {
        return changeRoleStatus(row.roleId, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function () {
        row.status = row.status === "0" ? "1" : "0";
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 取消按钮（数据权限）
    cancelDataScope() {
      this.openDataScope = false;
      this.reset();
    },
    // 表单重置
    reset() {
      if (this.$refs.menu != undefined) {
        this.$refs.menu.setCheckedKeys([]);
      }
      this.menuExpand = false,
        this.menuNodeAll = false,
        this.deptExpand = true,
        this.deptNodeAll = false,
        this.form = {
          roleId: undefined,
          roleName: undefined,
          roleKey: undefined,
          roleSort: 0,
          status: "0",
          menuIds: [],
          deptIds: [],
          menuCheckStrictly: true,
          deptCheckStrictly: true,
          remark: undefined
        };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.roleId)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    // 更多操作触发
    handleCommand(command, row) {
      switch (command) {
        case "handleDataScope":
          this.handleDataScope(row);
          break;
        case "handleAuthUser":
          this.handleAuthUser(row);
          break;
        default:
          break;
      }
    },
    // 树权限（展开/折叠）
    handleCheckedTreeExpand(value, type) {
      if (type == 'menu') {
        let treeList = this.menuOptions;
        for (let i = 0; i < treeList.length; i++) {
          this.$refs.menu.store.nodesMap[treeList[i].id].expanded = value;
        }
      } else if (type == 'dept') {
        let treeList = this.deptOptions;
        for (let i = 0; i < treeList.length; i++) {
          this.$refs.dept.store.nodesMap[treeList[i].id].expanded = value;
        }
      }
    },
    // 树权限（全选/全不选）
    handleCheckedTreeNodeAll(value, type) {
      if (type == 'menu') {
        this.$refs.menu.setCheckedNodes(value ? this.menuOptions : []);
      } else if (type == 'dept') {
        this.$refs.dept.setCheckedNodes(value ? this.deptOptions : []);
      }
    },
    // 树权限（父子联动）
    handleCheckedTreeConnect(value, type) {
      if (type == 'menu') {
        this.form.menuCheckStrictly = value ? true : false;
      } else if (type == 'dept') {
        this.form.deptCheckStrictly = value ? true : false;
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.getMenuTreeselect();
      this.open = true;
      this.title = "添加角色";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const roleId = row.id || this.ids
      this.form.id = roleId;
      this.open = true;
      this.title = '修改积分';


    },
    /** 选择角色权限范围触发 */
    dataScopeSelectChange(value) {
      if (value !== '2') {
        this.$refs.dept.setCheckedKeys([]);
      }
    },
    /** 分配数据权限操作 */
    handleDataScope(row) {
      this.reset();
      const deptTreeSelect = this.getDeptTree(row.roleId);
      getRole(row.roleId).then(response => {
        this.form = response.data;
        this.openDataScope = true;
        this.$nextTick(() => {
          deptTreeSelect.then(res => {
            this.$refs.dept.setCheckedKeys(res.checkedKeys);
          });
        });
        this.title = "分配数据权限";
      });
    },
    /** 分配用户操作 */
    handleAuthUser: function (row) {
      const roleId = row.roleId;
      this.$router.push("/system/role-auth/user/" + roleId);
    },
    /** 提交按钮 */
    submitForm: function (num) {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (num > 0) {
            addPoint(this.form).then(res => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            })
          } else {
            reducePoint(this.form).then(res => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            })
          }
          // updatePoint(this.form).then(response => {
          //     this.$modal.msgSuccess("修改成功");
          //     this.open = false;
          //   this.getList();
          // });
        }
      });
    },
    /** 提交按钮（数据权限） */
    submitDataScope: function () {
      if (this.form.roleId != undefined) {
        this.form.deptIds = this.getDeptAllCheckedKeys();
        dataScope(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.openDataScope = false;
          this.getList();
        });
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const roleIds = row.qid || this.ids;
      this.$modal.confirm('是否确认删除角色编号为"' + roleIds + '"的数据项？').then(function () {
        return delDept(roleIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/points/export', {
        ...this.queryParams
      }, `point_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>