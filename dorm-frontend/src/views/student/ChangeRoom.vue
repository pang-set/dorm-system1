<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">调宿申请</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="学生">
        <el-input v-model="searchForm.studentId" placeholder="请输入学生ID" clearable></el-input>
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
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增申请</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
      <el-table-column prop="studentNo" label="学号" width="120"></el-table-column>
      <el-table-column prop="oldRoomNo" label="原宿舍" width="100"></el-table-column>
      <el-table-column prop="newRoomNo" label="新宿舍" width="100"></el-table-column>
      <el-table-column prop="reason" label="申请原因"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.status)" size="small">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="160"></el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="isAdmin && scope.row.status === 0" type="text" size="small" @click="handleApprove(scope.row)">通过</el-button>
          <el-button v-if="isAdmin && scope.row.status === 0" type="text" size="small" style="color: #f56c6c" @click="handleReject(scope.row)">拒绝</el-button>
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="560px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="学生">
          <el-select
            v-model="form.studentId"
            placeholder="请选择学生"
            filterable
            remote
            :remote-method="searchStudent"
            :loading="studentLoading"
            style="width: 100%"
            @change="handleStudentChange">
            <el-option
              v-for="item in studentOptions"
              :key="item.id"
              :label="`${item.name} (${item.studentNo})`"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="新楼栋" prop="newBuildingId">
          <el-select v-model="form.newBuildingId" placeholder="请选择新楼栋" style="width: 100%" @change="handleNewBuildingChange">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="新宿舍" prop="newRoomId">
          <el-select v-model="form.newRoomId" placeholder="请选择新宿舍" style="width: 100%" @change="handleNewRoomChange" :disabled="!form.newBuildingId">
            <el-option v-for="item in availableRooms" :key="item.id" :label="item.roomNo" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="新床位" prop="newBedId">
          <el-select v-model="form.newBedId" placeholder="请选择新床位" style="width: 100%" :disabled="!form.newRoomId">
            <el-option v-for="item in availableBeds" :key="item.id" :label="item.bedNo" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="申请原因" prop="reason">
          <el-input type="textarea" v-model="form.reason" :rows="3" maxlength="200" show-word-limit placeholder="请详细说明调宿原因"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog title="查看详情" :visible.sync="viewDialogVisible" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生姓名">{{ viewData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ viewData.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="原宿舍">{{ viewData.oldRoomNo }}</el-descriptions-item>
        <el-descriptions-item label="新宿舍">{{ viewData.newRoomNo }}</el-descriptions-item>
        <el-descriptions-item label="申请原因">{{ viewData.reason }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(viewData.status)" size="small">
            {{ getStatusText(viewData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ viewData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getChangeRoomList, addChangeRoom, deleteChangeRoom, approveChangeRoom } from '@/api/changeRoom'
import { getAllBuildings } from '@/api/building'
import { getAvailableRooms, getRoomBeds } from '@/api/room'
import { getStudentList } from '@/api/student'

export default {
  name: 'ChangeRoom',
  data() {
    return {
      searchForm: {
        studentId: '',
        status: ''
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      viewDialogVisible: false,
      viewData: {},
      form: {
        id: null,
        studentId: null,
        newBuildingId: null,
        newRoomId: null,
        newBedId: null,
        reason: ''
      },
      rules: {
        studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
        newBuildingId: [{ required: true, message: '请选择新楼栋', trigger: 'change' }],
        newRoomId: [{ required: true, message: '请选择新宿舍', trigger: 'change' }],
        newBedId: [{ required: true, message: '请选择新床位', trigger: 'change' }],
        reason: [{ required: true, message: '请输入申请原因', trigger: 'blur' }]
      },
      buildingList: [],
      availableRooms: [],
      availableBeds: [],
      studentOptions: [],
      studentLoading: false
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
    async loadData() {
      this.loading = true
      try {
        const res = await getChangeRoomList({
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
      this.searchForm = { studentId: '', status: '' }
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
      this.dialogTitle = '新增调宿申请'
      this.form = {
        id: null,
        studentId: null,
        newBuildingId: null,
        newRoomId: null,
        newBedId: null,
        reason: ''
      }
      this.availableRooms = []
      this.availableBeds = []
      this.loadStudentOptions()
      this.dialogVisible = true
    },
    async loadBuildings() {
      const res = await getAllBuildings()
      if (res.code === 200) {
        this.buildingList = res.data
      }
    },
    async loadStudentOptions(keyword = '') {
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
    searchStudent(keyword) {
      this.loadStudentOptions(keyword)
    },
    handleStudentChange() {
      // 选择学生时不做特殊处理，让用户自己选新楼栋宿舍
    },
    async handleNewBuildingChange() {
      this.form.newRoomId = null
      this.form.newBedId = null
      this.availableBeds = []
      if (this.form.newBuildingId) {
        const res = await getAvailableRooms({ buildingId: this.form.newBuildingId })
        if (res.code === 200) {
          this.availableRooms = res.data
        }
      } else {
        this.availableRooms = []
      }
    },
    async handleNewRoomChange() {
      this.form.newBedId = null
      if (this.form.newRoomId) {
        const res = await getRoomBeds(this.form.newRoomId)
        if (res.code === 200) {
          this.availableBeds = (res.data || []).filter(bed => bed.status === 0)
        }
      } else {
        this.availableBeds = []
      }
    },
    handleView(row) {
      this.viewData = { ...row }
      this.viewDialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = await addChangeRoom(this.form)
          if (res.code === 200) {
            this.$message.success('申请提交成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleApprove(row) {
      this.$confirm('确定通过该调宿申请吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await approveChangeRoom(row.id, { status: 1 })
        if (res.code === 200) {
          this.$message.success('审批通过')
          this.loadData()
        }
      }).catch(() => {})
    },
    handleReject(row) {
      this.$confirm('确定拒绝该调宿申请吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await approveChangeRoom(row.id, { status: 2 })
        if (res.code === 200) {
          this.$message.success('已拒绝')
          this.loadData()
        }
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$confirm('确定要删除该申请吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteChangeRoom(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    }
  }
}
</script>
