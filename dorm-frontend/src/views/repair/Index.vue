<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">报修管理</h2>
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
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待处理" :value="1"></el-option>
          <el-option label="处理中" :value="2"></el-option>
          <el-option label="已完成" :value="3"></el-option>
          <el-option label="已评价" :value="4"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="报修类型">
        <el-select v-model="searchForm.repairType" placeholder="请选择" clearable>
          <el-option label="水电维修" value="水电维修"></el-option>
          <el-option label="家具维修" value="家具维修"></el-option>
          <el-option label="门窗维修" value="门窗维修"></el-option>
          <el-option label="网络维修" value="网络维修"></el-option>
          <el-option label="其他" value="其他"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增报修</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="repairNo" label="报修编号" width="150"></el-table-column>
      <el-table-column prop="studentName" label="学生姓名" width="100"></el-table-column>
      <el-table-column label="楼栋" width="120">
        <template slot-scope="scope">
          {{ getBuildingName(scope.row.buildingId) }}
        </template>
      </el-table-column>
      <el-table-column prop="roomNo" label="宿舍号" width="100"></el-table-column>
      <el-table-column prop="repairType" label="报修类型" width="120"></el-table-column>
      <el-table-column prop="title" label="标题" min-width="150"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.status)" size="small">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status === 1" type="text" size="small" @click="handleAssign(scope.row)">派单</el-button>
          <el-button v-if="scope.row.status === 2" type="text" size="small" @click="handleComplete(scope.row)">完成</el-button>
          <el-button v-if="scope.row.status === 3" type="text" size="small" @click="handleRate(scope.row)">评价</el-button>
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" placeholder="请选择学生" filterable remote :remote-method="searchStudent" :loading="studentLoading" style="width: 100%">
            <el-option v-for="item in studentOptions" :key="item.id" :label="`${item.name} (${item.studentNo})`" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍号" prop="roomNo">
          <el-input v-model="form.roomNo"></el-input>
        </el-form-item>
        <el-form-item label="报修类型" prop="repairType">
          <el-select v-model="form.repairType" style="width: 100%">
            <el-option label="水电维修" value="水电维修"></el-option>
            <el-option label="家具维修" value="家具维修"></el-option>
            <el-option label="门窗维修" value="门窗维修"></el-option>
            <el-option label="网络维修" value="网络维修"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="派单" :visible.sync="assignDialogVisible" width="500px">
      <el-form :model="assignForm" :rules="assignRules" ref="assignForm" label-width="100px">
        <el-form-item label="维修人员ID" prop="repairerId">
          <el-input v-model="assignForm.repairerId"></el-input>
        </el-form-item>
        <el-form-item label="维修人员姓名" prop="repairerName">
          <el-input v-model="assignForm.repairerName"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="完成报修" :visible.sync="completeDialogVisible" width="500px">
      <el-form :model="completeForm" :rules="completeRules" ref="completeForm" label-width="100px">
        <el-form-item label="维修结果" prop="repairResult">
          <el-input type="textarea" v-model="completeForm.repairResult" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCompleteSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="评价" :visible.sync="rateDialogVisible" width="500px">
      <el-form :model="rateForm" :rules="rateRules" ref="rateForm" label-width="100px">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="rateForm.rating" :max="5" show-text></el-rate>
        </el-form-item>
        <el-form-item label="评价内容" prop="rateContent">
          <el-input type="textarea" v-model="rateForm.rateContent" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRateSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="查看详情" :visible.sync="viewDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报修编号">{{ viewData.repairNo }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ viewData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="楼栋">{{ getBuildingName(viewData.buildingId) }}</el-descriptions-item>
        <el-descriptions-item label="宿舍号">{{ viewData.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">{{ viewData.repairType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(viewData.status)" size="small">
            {{ getStatusText(viewData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ viewData.description }}</el-descriptions-item>
        <el-descriptions-item label="维修人员">{{ viewData.repairerName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="维修结果" :span="2">{{ viewData.repairResult }}</el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate v-if="viewData.rating" :value="viewData.rating" disabled show-score text-color="#ff9900" score-template="{value}分"></el-rate>
        </el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">{{ viewData.rateContent }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getRepairList, addRepair, updateRepair, deleteRepair, assignRepair, completeRepair, rateRepair } from '@/api/repair'
import { getAllBuildings } from '@/api/building'
import { getStudentList } from '@/api/student'

export default {
  name: 'Repair',
  data() {
    return {
      searchForm: {
        studentId: '',
        buildingId: '',
        status: '',
        repairType: ''
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
      dialogTitle: '',
      isEdit: false,
      viewDialogVisible: false,
      viewData: {},
      assignDialogVisible: false,
      assignForm: {
        id: null,
        repairerId: '',
        repairerName: ''
      },
      completeDialogVisible: false,
      completeForm: {
        id: null,
        repairResult: ''
      },
      rateDialogVisible: false,
      rateForm: {
        id: null,
        rating: 5,
        rateContent: ''
      },
      form: {
        id: null,
        studentId: '',
        buildingId: '',
        roomNo: '',
        repairType: '水电维修',
        title: '',
        description: ''
      },
      rules: {
        studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
        buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
        roomNo: [{ required: true, message: '请输入宿舍号', trigger: 'blur' }],
        repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }]
      },
      assignRules: {
        repairerId: [{ required: true, message: '请输入维修人员ID', trigger: 'blur' }],
        repairerName: [{ required: true, message: '请输入维修人员姓名', trigger: 'blur' }]
      },
      completeRules: {
        repairResult: [{ required: true, message: '请输入维修结果', trigger: 'blur' }]
      },
      rateRules: {
        rating: [{ required: true, message: '请选择评分', trigger: 'change' }]
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
    getStatusText(status) {
      const map = { 1: '待处理', 2: '处理中', 3: '已完成', 4: '已评价' }
      return map[status] || '未知'
    },
    getStatusTagType(status) {
      const map = { 1: 'warning', 2: 'primary', 3: 'success', 4: 'info' }
      return map[status] || 'info'
    },
    getBuildingName(buildingId) {
      const b = this.buildingList.find(item => item.id === buildingId)
      return b ? b.buildingName : (buildingId || '')
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getRepairList({
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
      this.searchForm = { studentId: '', buildingId: '', status: '', repairType: '' }
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
      this.dialogTitle = '新增报修'
      this.form = {
        id: null,
        studentId: '',
        buildingId: '',
        roomNo: '',
        repairType: '水电维修',
        title: '',
        description: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑报修'
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
            ? await updateRepair(this.form)
            : await addRepair(this.form)

          if (res.code === 200) {
            this.$message.success(this.isEdit ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该报修记录吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteRepair(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    },
    handleAssign(row) {
      this.assignForm = {
        id: row.id,
        repairerId: '',
        repairerName: ''
      }
      this.assignDialogVisible = true
    },
    async handleAssignSubmit() {
      this.$refs.assignForm.validate(async valid => {
        if (valid) {
          const res = await assignRepair(this.assignForm.id, this.assignForm)
          if (res.code === 200) {
            this.$message.success('派单成功')
            this.assignDialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleComplete(row) {
      this.completeForm = {
        id: row.id,
        repairResult: ''
      }
      this.completeDialogVisible = true
    },
    async handleCompleteSubmit() {
      this.$refs.completeForm.validate(async valid => {
        if (valid) {
          const res = await completeRepair(this.completeForm.id, this.completeForm)
          if (res.code === 200) {
            this.$message.success('完成成功')
            this.completeDialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleRate(row) {
      this.rateForm = {
        id: row.id,
        rating: 5,
        rateContent: ''
      }
      this.rateDialogVisible = true
    },
    async handleRateSubmit() {
      this.$refs.rateForm.validate(async valid => {
        if (valid) {
          const res = await rateRepair(this.rateForm.id, this.rateForm)
          if (res.code === 200) {
            this.$message.success('评价成功')
            this.rateDialogVisible = false
            this.loadData()
          }
        }
      })
    }
  }
}
</script>
