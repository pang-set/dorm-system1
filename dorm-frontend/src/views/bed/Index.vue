<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">床位管理</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="楼栋">
        <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable @change="handleBuildingChange">
          <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="宿舍">
        <el-select v-model="searchForm.roomId" placeholder="请选择" clearable>
          <el-option v-for="item in roomList" :key="item.id" :label="item.roomNo" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="空闲" :value="0"></el-option>
          <el-option label="已入住" :value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增床位</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="bedNo" label="床位号" width="120"></el-table-column>
      <el-table-column label="楼栋" width="120">
        <template slot-scope="scope">
          {{ getBuildingName(scope.row.buildingId) }}
        </template>
      </el-table-column>
      <el-table-column label="宿舍" width="100">
        <template slot-scope="scope">
          {{ getRoomNo(scope.row.roomId) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'warning'" size="small">
            {{ scope.row.status === 0 ? '空闲' : '已入住' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学生" width="120">
        <template slot-scope="scope">
          {{ getStudentName(scope.row.studentId) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
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
        <el-form-item label="宿舍" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请选择宿舍" style="width: 100%">
            <el-option v-for="item in roomList" :key="item.id" :label="item.roomNo" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="床位号" prop="bedNo">
          <el-input v-model="form.bedNo"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">空闲</el-radio>
            <el-radio :label="1">已入住</el-radio>
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
import { getBedList, addBed, updateBed, deleteBed } from '@/api/bed'
import { getAllBuildings } from '@/api/building'
import { getRoomList } from '@/api/room'
import { getStudentList } from '@/api/student'

export default {
  name: 'Bed',
  data() {
    return {
      searchForm: {
        buildingId: '',
        roomId: '',
        status: ''
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      buildingList: [],
      roomList: [],
      allRooms: [],
      allStudents: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: {
        id: null,
        roomId: '',
        bedNo: '',
        status: 0
      },
      rules: {
        roomId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
        bedNo: [{ required: true, message: '请输入床位号', trigger: 'blur' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.loadData()
    this.loadBuildings()
    this.loadAllRooms()
    this.loadAllStudents()
  },
  methods: {
    async loadBuildings() {
      const res = await getAllBuildings()
      if (res.code === 200) {
        this.buildingList = res.data
      }
    },
    async loadRooms(buildingId) {
      if (!buildingId) {
        this.roomList = []
        return
      }
      const res = await getRoomList({ buildingId, pageNum: 1, pageSize: 1000 })
      if (res.code === 200) {
        this.roomList = res.data.records
      }
    },
    async loadAllRooms() {
      const res = await getRoomList({ pageNum: 1, pageSize: 1000 })
      if (res.code === 200) {
        this.allRooms = res.data.records || []
      }
    },
    async loadAllStudents() {
      const res = await getStudentList({ pageNum: 1, pageSize: 1000 })
      if (res.code === 200) {
        this.allStudents = res.data.records || []
      }
    },
    getBuildingName(buildingId) {
      const b = this.buildingList.find(item => item.id === buildingId)
      return b ? b.buildingName : (buildingId || '')
    },
    getRoomNo(roomId) {
      const r = this.allRooms.find(item => item.id === roomId)
      return r ? r.roomNo : (roomId || '')
    },
    getStudentName(studentId) {
      const s = this.allStudents.find(item => item.id === studentId)
      return s ? s.name : (studentId || '')
    },
    handleBuildingChange(val) {
      this.searchForm.roomId = ''
      this.loadRooms(val)
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getBedList({
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
      this.searchForm = { buildingId: '', roomId: '', status: '' }
      this.roomList = []
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
      this.dialogTitle = '新增床位'
      this.form = {
        id: null,
        roomId: '',
        bedNo: '',
        status: 0
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑床位'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = this.isEdit
            ? await updateBed(this.form)
            : await addBed(this.form)

          if (res.code === 200) {
            this.$message.success(this.isEdit ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该床位吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteBed(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    }
  }
}
</script>
