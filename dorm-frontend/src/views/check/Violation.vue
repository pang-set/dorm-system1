<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">违纪记录</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="学生">
        <el-select
          v-model="searchForm.studentId"
          placeholder="请选择学生"
          clearable
          filterable
          remote
          :remote-method="searchStudent"
          :loading="studentLoading">
          <el-option
            v-for="item in studentOptions"
            :key="item.id"
            :label="`${item.name} (${item.studentNo})`"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="楼栋">
        <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
          <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="违纪类型">
        <el-select v-model="searchForm.violationType" placeholder="请选择" clearable>
          <el-option v-for="item in violationTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待处理" :value="0"></el-option>
          <el-option label="已处理" :value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增违纪</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
      <el-table-column prop="studentNo" label="学号" width="120"></el-table-column>
      <el-table-column label="楼栋" width="120">
        <template slot-scope="scope">
          {{ getBuildingName(scope.row.buildingId) }}
        </template>
      </el-table-column>
      <el-table-column prop="violationType" label="违纪类型" width="120">
        <template slot-scope="scope">
          {{ getViolationTypeText(scope.row.violationType) }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="violationTime" label="违纪时间" width="160"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 0 ? 'warning' : 'success'" size="small">
            {{ scope.row.status === 0 ? '待处理' : '已处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="handleResult" label="处理结果" width="150"></el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status === 0" type="text" size="small" @click="handleHandle(scope.row)">处理</el-button>
          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
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
        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="form.studentId"
            placeholder="请选择学生"
            filterable
            remote
            :remote-method="searchStudent"
            :loading="studentLoading"
            style="width: 100%">
            <el-option
              v-for="item in studentOptions"
              :key="item.id"
              :label="`${item.name} (${item.studentNo})`"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="违纪类型" prop="violationType">
          <el-select v-model="form.violationType" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in violationTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="违纪时间" prop="violationTime">
          <el-date-picker
            v-model="form.violationTime"
            type="datetime"
            placeholder="选择日期时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="处理违纪" :visible.sync="handleDialogVisible" width="500px">
      <el-form :model="handleForm" :rules="handleRules" ref="handleForm" label-width="100px">
        <el-form-item label="处理结果" prop="handleResult">
          <el-input type="textarea" v-model="handleForm.handleResult" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleHandleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getViolationList, addViolation, updateViolation, deleteViolation, handleViolation } from '@/api/violation'
import { getAllBuildings } from '@/api/building'
import { getStudentList } from '@/api/student'

export default {
  name: 'Violation',
  data() {
    return {
      searchForm: {
        studentId: '',
        buildingId: '',
        violationType: '',
        status: ''
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      buildingList: [],
      studentOptions: [],
      studentLoading: false,
      violationTypeOptions: [
        { label: '晚归', value: '晚归' },
        { label: '夜不归宿', value: '夜不归宿' },
        { label: '使用违禁电器', value: '使用违禁电器' },
        { label: '吸烟', value: '吸烟' },
        { label: '吵闹喧哗', value: '吵闹喧哗' },
        { label: '其他', value: '其他' }
      ],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      handleDialogVisible: false,
      handleForm: {
        id: null,
        handleResult: '',
        status: 1
      },
      form: {
        id: null,
        studentId: '',
        violationType: '',
        description: '',
        violationTime: ''
      },
      rules: {
        studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
        violationType: [{ required: true, message: '请选择违纪类型', trigger: 'change' }],
        description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
        violationTime: [{ required: true, message: '请选择违纪时间', trigger: 'change' }]
      },
      handleRules: {
        handleResult: [{ required: true, message: '请输入处理结果', trigger: 'blur' }]
      }
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
    async searchStudent(keyword) {
      this.studentLoading = true
      try {
        const res = await getStudentList({ pageNum: 1, pageSize: 50, name: keyword, studentNo: keyword })
        if (res.code === 200) {
          this.studentOptions = res.data.records || []
        }
      } finally {
        this.studentLoading = false
      }
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getViolationList({
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
    getViolationTypeText(type) {
      const item = this.violationTypeOptions.find(opt => opt.value === type)
      return item ? item.label : type
    },
    getBuildingName(buildingId) {
      const b = this.buildingList.find(item => item.id === buildingId)
      return b ? b.buildingName : (buildingId || '')
    },
    handleSearch() {
      this.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { studentId: '', buildingId: '', violationType: '', status: '' }
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
      this.dialogTitle = '新增违纪记录'
      this.form = {
        id: null,
        studentId: '',
        violationType: '',
        description: '',
        violationTime: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑违纪记录'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = this.isEdit
            ? await updateViolation(this.form)
            : await addViolation(this.form)

          if (res.code === 200) {
            this.$message.success(this.isEdit ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleHandle(row) {
      this.handleForm = {
        id: row.id,
        handleResult: '',
        status: 1
      }
      this.handleDialogVisible = true
    },
    async handleHandleSubmit() {
      this.$refs.handleForm.validate(async valid => {
        if (valid) {
          const res = await handleViolation(this.handleForm.id, this.handleForm)
          if (res.code === 200) {
            this.$message.success('处理成功')
            this.handleDialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该违纪记录吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteViolation(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    }
  }
}
</script>
