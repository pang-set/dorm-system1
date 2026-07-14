<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">晚归登记</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="学生">
        <el-select v-model="searchForm.studentId" placeholder="请选择学生" clearable filterable remote :remote-method="searchStudent" :loading="studentLoading">
          <el-option v-for="item in studentOptions" :key="item.id" :label="`${item.name} (${item.studentNo})`" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="楼栋">
        <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
          <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开始日期">
        <el-date-picker
          v-model="searchForm.startDate"
          type="date"
          placeholder="选择开始日期"
          style="width: 180px">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束日期">
        <el-date-picker
          v-model="searchForm.endDate"
          type="date"
          placeholder="选择结束日期"
          style="width: 180px">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增登记</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
      <el-table-column prop="studentNo" label="学号" width="120"></el-table-column>
      <el-table-column label="楼栋" width="120">
        <template slot-scope="scope">
          {{ getBuildingName(scope.row.buildingId) }}
        </template>
      </el-table-column>
      <el-table-column prop="returnTime" label="归寝时间" width="160"></el-table-column>
      <el-table-column prop="reason" label="原因"></el-table-column>
      <el-table-column prop="registrarName" label="登记人" width="100"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template slot-scope="scope">
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

    <el-dialog title="新增晚归登记" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" placeholder="请选择学生" filterable remote :remote-method="searchStudent" :loading="studentLoading" style="width: 100%">
            <el-option v-for="item in studentOptions" :key="item.id" :label="`${item.name} (${item.studentNo})`" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="归寝时间" prop="returnTime">
          <el-date-picker
            v-model="form.returnTime"
            type="datetime"
            placeholder="选择日期时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input type="textarea" v-model="form.reason" :rows="3"></el-input>
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
import { getLateReturnList, addLateReturn, deleteLateReturn } from '@/api/lateReturn'
import { getAllBuildings } from '@/api/building'
import { getStudentList } from '@/api/student'

export default {
  name: 'LateReturn',
  data() {
    return {
      searchForm: {
        studentId: '',
        buildingId: '',
        startDate: '',
        endDate: ''
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      buildingList: [],
      studentOptions: [],
      studentLoading: false,
      dialogVisible: false,
      form: {
        id: null,
        studentId: '',
        returnTime: '',
        reason: ''
      },
      rules: {
        studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
        returnTime: [{ required: true, message: '请选择归寝时间', trigger: 'change' }],
        reason: [{ required: true, message: '请输入原因', trigger: 'blur' }]
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
    getBuildingName(buildingId) {
      const b = this.buildingList.find(item => item.id === buildingId)
      return b ? b.buildingName : (buildingId || '')
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getLateReturnList({
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
    handleSearch() {
      this.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { studentId: '', buildingId: '', startDate: '', endDate: '' }
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
      this.form = {
        id: null,
        studentId: '',
        returnTime: '',
        reason: ''
      }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = await addLateReturn(this.form)
          if (res.code === 200) {
            this.$message.success('登记成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该晚归登记吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteLateReturn(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    }
  }
}
</script>
