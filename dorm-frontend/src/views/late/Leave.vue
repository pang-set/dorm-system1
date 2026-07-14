<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">请假报备</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="学生">
        <el-input v-model="searchForm.studentId" placeholder="请输入学生ID" clearable></el-input>
      </el-form-item>
      <el-form-item label="楼栋">
        <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
          <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待审批" :value="0"></el-option>
          <el-option label="已通过" :value="1"></el-option>
          <el-option label="已拒绝" :value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button v-if="!isAdmin" type="primary" icon="el-icon-plus" @click="handleAdd">新增请假</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
      <el-table-column prop="studentNo" label="学号" width="120"></el-table-column>
      <el-table-column prop="startDate" label="开始日期" width="120"></el-table-column>
      <el-table-column prop="endDate" label="结束日期" width="120"></el-table-column>
      <el-table-column prop="reason" label="请假原因"></el-table-column>
      <el-table-column prop="destination" label="目的地" width="120"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.status)" size="small">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="approverName" label="审批人" width="100"></el-table-column>
      <el-table-column prop="approveTime" label="审批时间" width="160"></el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="isAdmin && scope.row.status === 0" type="text" size="small" @click="handleApprove(scope.row)">审批</el-button>
          <el-button v-if="!isAdmin && scope.row.status === 0" type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button v-if="!isAdmin" type="text" size="small" style="color: #f56c6c" @click="handleDelete(scope.row)">删除</el-button>
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
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="请假原因" prop="reason">
          <el-input type="textarea" v-model="form.reason" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="目的地" prop="destination">
          <el-input v-model="form.destination"></el-input>
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="form.emergencyContact"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="审批请假" :visible.sync="approveDialogVisible" width="500px">
      <el-form :model="approveForm" :rules="approveRules" ref="approveForm" label-width="100px">
        <el-form-item label="审批结果" prop="status">
          <el-radio-group v-model="approveForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批备注" prop="approveRemark">
          <el-input type="textarea" v-model="approveForm.approveRemark" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleApproveSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="查看详情" :visible.sync="viewDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生姓名">{{ viewData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ viewData.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ viewData.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ viewData.endDate }}</el-descriptions-item>
        <el-descriptions-item label="目的地">{{ viewData.destination }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ viewData.emergencyContact }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(viewData.status)" size="small">
            {{ getStatusText(viewData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批人">{{ viewData.approverName }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ viewData.approveTime }}</el-descriptions-item>
        <el-descriptions-item label="请假原因" :span="2">{{ viewData.reason }}</el-descriptions-item>
        <el-descriptions-item label="审批备注" :span="2">{{ viewData.approveRemark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getLeaveList, addLeave, updateLeave, deleteLeave, approveLeave } from '@/api/lateReturn'
import { getAllBuildings } from '@/api/building'

export default {
  name: 'Leave',
  data() {
    return {
      searchForm: {
        studentId: '',
        buildingId: '',
        status: ''
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      buildingList: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      approveDialogVisible: false,
      viewDialogVisible: false,
      viewData: {},
      approveForm: {
        id: null,
        status: 1,
        approveRemark: ''
      },
      form: {
        id: null,
        startDate: '',
        endDate: '',
        reason: '',
        destination: '',
        emergencyContact: ''
      },
      rules: {
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
        reason: [{ required: true, message: '请输入请假原因', trigger: 'blur' }],
        destination: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
        emergencyContact: [{ required: true, message: '请输入紧急联系人', trigger: 'blur' }]
      },
      approveRules: {
        status: [{ required: true, message: '请选择审批结果', trigger: 'change' }]
      }
    }
  },
  computed: {
    isAdmin() {
      return this.$store.getters.role === 'ADMIN'
    }
  },
  mounted() {
    this.loadData()
    this.loadBuildings()
  },
  methods: {
    async loadBuildings() {
      const res = await getAllBuildings()
      if (res.code === 200) {
        this.buildingList = res.data
      }
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getLeaveList({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          ...this.searchForm
        })
        if (res.code === 200) {
          this.tableData = res.data.records || res.data.list || []
          this.total = res.data.total || 0
        }
      } finally {
        this.loading = false
      }
    },
    getStatusText(status) {
      const map = { 0: '待审批', 1: '已通过', 2: '已拒绝' }
      return map[status] !== undefined ? map[status] : '未知'
    },
    getStatusTagType(status) {
      const map = { 0: 'warning', 1: 'success', 2: 'danger' }
      return map[status] || 'info'
    },
    handleSearch() {
      this.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { studentId: '', buildingId: '', status: '' }
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
      this.dialogTitle = '新增请假'
      this.form = {
        id: null,
        startDate: '',
        endDate: '',
        reason: '',
        destination: '',
        emergencyContact: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑请假'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleView(row) {
      this.viewData = { ...row }
      this.viewDialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = this.isEdit
            ? await updateLeave(this.form)
            : await addLeave(this.form)

          if (res.code === 200) {
            this.$message.success(this.isEdit ? '修改成功' : '提交成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleApprove(row) {
      this.approveForm = {
        id: row.id,
        status: 1,
        approveRemark: ''
      }
      this.approveDialogVisible = true
    },
    async handleApproveSubmit() {
      this.$refs.approveForm.validate(async valid => {
        if (valid) {
          const res = await approveLeave(this.approveForm)
          if (res.code === 200) {
            this.$message.success('审批成功')
            this.approveDialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该请假申请吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteLeave(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    }
  }
}
</script>
