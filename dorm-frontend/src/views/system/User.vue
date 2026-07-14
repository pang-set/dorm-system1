<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable></el-input>
      </el-form-item>
      <el-form-item label="真实姓名">
        <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable></el-input>
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="searchForm.role" placeholder="请选择" clearable>
          <el-option label="管理员" value="ADMIN"></el-option>
          <el-option label="宿管" value="HOUSEMASTER"></el-option>
          <el-option label="学生" value="STUDENT"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="正常" :value="1"></el-option>
          <el-option label="禁用" :value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增用户</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="username" label="用户名" width="120"></el-table-column>
      <el-table-column prop="realName" label="真实姓名" width="120"></el-table-column>
      <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
      <el-table-column prop="role" label="角色" width="100">
        <template slot-scope="scope">
          <el-tag :type="getRoleType(scope.row.role)" size="small">{{ getRoleName(scope.row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="buildingId" label="管理楼栋" width="100"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="handleResetPwd(scope.row)">重置密码</el-button>
          <el-button type="text" size="small" style="color: #f56c6c" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange">
      </el-pagination>
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit" prop="password">
          <el-input v-model="form.password" type="password" placeholder="默认123456"></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="宿管" value="HOUSEMASTER"></el-option>
            <el-option label="学生" value="STUDENT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, addUser, updateUser, deleteUser, resetPassword } from '@/api/user'

export default {
  name: 'UserManage',
  data() {
    return {
      searchForm: {
        username: '',
        realName: '',
        role: '',
        status: null
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: {
        id: null,
        username: '',
        password: '',
        realName: '',
        phone: '',
        role: 'STUDENT',
        status: 1,
        buildingId: null
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    getRoleName(role) {
      const map = { ADMIN: '管理员', HOUSEMASTER: '宿管', STUDENT: '学生' }
      return map[role] || role
    },
    getRoleType(role) {
      const map = { ADMIN: 'danger', HOUSEMASTER: 'warning', STUDENT: 'primary' }
      return map[role] || 'info'
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getUserList({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          ...this.searchForm
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { username: '', realName: '', role: '', status: null }
      this.handleSearch()
    },
    handlePageChange(page) {
      this.pageNum = page
      this.loadData()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.pageNum = 1
      this.loadData()
    },
    handleAdd() {
      this.isEdit = false
      this.dialogTitle = '新增用户'
      this.form = {
        id: null,
        username: '',
        password: '',
        realName: '',
        phone: '',
        role: 'STUDENT',
        status: 1,
        buildingId: null
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑用户'
      this.form = { ...row, password: '' }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = this.isEdit
            ? await updateUser(this.form)
            : await addUser(this.form)

          if (res.code === 200) {
            this.$message.success(this.isEdit ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该用户吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteUser(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    },
    handleResetPwd(row) {
      this.$confirm('确定要重置该用户的密码吗？重置后密码为123456', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await resetPassword(row.id)
        if (res.code === 200) {
          this.$message.success('密码重置成功')
        }
      }).catch(() => {})
    }
  }
}
</script>
