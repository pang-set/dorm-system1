<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">学生管理</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="学号">
        <el-input v-model="searchForm.studentNo" placeholder="请输入学号" clearable></el-input>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable></el-input>
      </el-form-item>
      <el-form-item label="楼栋">
        <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable @change="handleBuildingFilter">
          <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="宿舍">
        <el-select v-model="searchForm.roomId" placeholder="请选择" clearable filterable>
          <el-option v-for="item in filteredRoomList" :key="item.id" :label="item.roomNo" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="学院">
        <el-select v-model="searchForm.college" placeholder="请选择" clearable filterable>
          <el-option v-for="item in collegeList" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="未入住" :value="1"></el-option>
          <el-option label="已入住" :value="2"></el-option>
          <el-option label="已退宿" :value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增学生</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="studentNo" label="学号" width="120"></el-table-column>
      <el-table-column prop="name" label="姓名" width="100"></el-table-column>
      <el-table-column prop="gender" label="性别" width="80">
        <template slot-scope="scope">
          {{ scope.row.gender === 'male' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="college" label="学院" width="120"></el-table-column>
      <el-table-column prop="major" label="专业" width="120"></el-table-column>
      <el-table-column prop="className" label="班级" width="100"></el-table-column>
      <el-table-column prop="grade" label="年级" width="80"></el-table-column>
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
          <el-tag :type="getStatusTagType(scope.row.status)" size="small">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status === 1" type="text" size="small" @click="handleCheckIn(scope.row)">入住</el-button>
          <el-button v-if="scope.row.status === 2" type="text" size="small" @click="handleCheckOut(scope.row)">退宿</el-button>
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
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证" prop="idCard">
          <el-input v-model="form.idCard"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="学院" prop="college">
          <el-select v-model="form.college" placeholder="请选择学院" filterable allow-create style="width: 100%">
            <el-option v-for="item in collegeList" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-select v-model="form.major" placeholder="请选择专业" filterable allow-create style="width: 100%">
            <el-option v-for="item in majorList" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="班级" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级，如：软件工程1班"></el-input>
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
            <el-option v-for="item in gradeList" :key="item" :label="`${item}级`" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input type="textarea" v-model="form.address" :rows="2"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="入住登记" :visible.sync="checkInDialogVisible" width="500px">
      <el-form :model="checkInForm" :rules="checkInRules" ref="checkInForm" label-width="100px">
        <el-form-item label="学生姓名">
          <el-input v-model="checkInForm.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="选择楼栋" prop="buildingId">
          <el-select v-model="checkInForm.buildingId" placeholder="请选择楼栋" style="width: 100%" @change="handleBuildingChange">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择宿舍" prop="roomId">
          <el-select v-model="checkInForm.roomId" placeholder="请选择宿舍" style="width: 100%" @change="handleRoomChange">
            <el-option v-for="item in availableRooms" :key="item.id" :label="item.roomNo" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择床位" prop="bedId">
          <el-select v-model="checkInForm.bedId" placeholder="请选择床位" style="width: 100%">
            <el-option v-for="item in availableBeds" :key="item.id" :label="item.bedNo" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="checkInDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCheckInSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getStudentList, addStudent, updateStudent, deleteStudent, checkIn, checkOut } from '@/api/student'
import { getAllBuildings } from '@/api/building'
import { getRoomBeds, getAvailableRooms, getRoomList } from '@/api/room'

export default {
  name: 'Student',
  data() {
    return {
      searchForm: {
        studentNo: '',
        name: '',
        buildingId: '',
        roomId: '',
        college: '',
        status: ''
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      buildingList: [],
      roomList: [],
      collegeList: [],
      majorList: [],
      gradeList: [2026, 2025, 2024, 2023, 2022, 2021, 2020],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      checkInDialogVisible: false,
      filteredRoomList: [],
      checkInForm: {
        studentId: null,
        studentName: '',
        buildingId: '',
        roomId: '',
        bedId: ''
      },
      availableRooms: [],
      availableBeds: [],
      form: {
        id: null,
        studentNo: '',
        name: '',
        gender: 'male',
        idCard: '',
        phone: '',
        college: '',
        major: '',
        className: '',
        grade: '',
        address: ''
      },
      rules: {
        studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
        idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
        college: [{ required: true, message: '请输入学院', trigger: 'blur' }],
        major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
        className: [{ required: true, message: '请输入班级', trigger: 'blur' }],
        grade: [{ required: true, message: '请输入年级', trigger: 'blur' }]
      },
      checkInRules: {
        buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
        roomId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
        bedId: [{ required: true, message: '请选择床位', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.loadData()
    this.loadBuildings()
    this.loadRooms()
    this.loadColleges()
    this.loadMajors()
  },
  methods: {
    async loadBuildings() {
      const res = await getAllBuildings()
      if (res.code === 200) {
        this.buildingList = res.data
      }
    },
    async loadRooms() {
      const res = await getRoomList({ pageNum: 1, pageSize: 1000 })
      if (res.code === 200) {
        this.roomList = res.data.records || res.data.list || []
        this.filteredRoomList = this.roomList
      }
    },
    async loadColleges() {
      try {
        const res = await getStudentList({ pageNum: 1, pageSize: 1000 })
        if (res.code === 200) {
          const colleges = new Set()
          ;(res.data.records || []).forEach(s => {
            if (s.college) colleges.add(s.college)
          })
          this.collegeList = Array.from(colleges).sort()
        }
      } catch (e) {
        this.collegeList = ['计算机学院', '软件学院', '电子信息学院', '机械学院', '外国语学院', '经济管理学院']
      }
    },
    async loadMajors() {
      try {
        const res = await getStudentList({ pageNum: 1, pageSize: 1000 })
        if (res.code === 200) {
          const majors = new Set()
          ;(res.data.records || []).forEach(s => {
            if (s.major) majors.add(s.major)
          })
          this.majorList = Array.from(majors).sort()
        }
      } catch (e) {
        this.majorList = ['计算机科学与技术', '软件工程', '电子信息工程', '机械设计制造及其自动化', '英语', '工商管理']
      }
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getStudentList({
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
    getStatusText(status) {
      const map = { 1: '未入住', 2: '已入住', 3: '已退宿' }
      return map[status] || '未知'
    },
    getStatusTagType(status) {
      const map = { 1: 'info', 2: 'success', 3: 'warning' }
      return map[status] || 'info'
    },
    getBuildingName(buildingId) {
      const b = this.buildingList.find(item => item.id === buildingId)
      return b ? b.buildingName : (buildingId || '')
    },
    getRoomNo(roomId) {
      const r = this.roomList.find(item => item.id === roomId)
      return r ? r.roomNo : (roomId || '')
    },
    handleSearch() {
      this.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { studentNo: '', name: '', buildingId: '', roomId: '', college: '', status: '' }
      this.filteredRoomList = this.roomList
      this.handleSearch()
    },
    handleBuildingFilter(val) {
      if (val) {
        this.filteredRoomList = this.roomList.filter(r => r.buildingId === val)
        this.searchForm.roomId = ''
      } else {
        this.filteredRoomList = this.roomList
      }
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
      this.dialogTitle = '新增学生'
      this.form = {
        id: null,
        studentNo: '',
        name: '',
        gender: 'male',
        idCard: '',
        phone: '',
        college: '',
        major: '',
        className: '',
        grade: '',
        address: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑学生'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = this.isEdit
            ? await updateStudent(this.form)
            : await addStudent(this.form)

          if (res.code === 200) {
            this.$message.success(this.isEdit ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该学生吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteStudent(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    },
    handleCheckIn(row) {
      this.checkInForm = {
        studentId: row.id,
        studentName: row.name,
        buildingId: '',
        roomId: '',
        bedId: ''
      }
      this.availableRooms = []
      this.availableBeds = []
      this.checkInDialogVisible = true
    },
    async handleBuildingChange() {
      this.checkInForm.roomId = ''
      this.checkInForm.bedId = ''
      this.availableBeds = []
      if (this.checkInForm.buildingId) {
        const res = await getAvailableRooms({ buildingId: this.checkInForm.buildingId })
        if (res.code === 200) {
          this.availableRooms = res.data
        }
      }
    },
    async handleRoomChange() {
      this.checkInForm.bedId = ''
      if (this.checkInForm.roomId) {
        const res = await getRoomBeds(this.checkInForm.roomId)
        if (res.code === 200) {
          this.availableBeds = res.data.filter(bed => bed.status === 0)
        }
      }
    },
    handleCheckInSubmit() {
      this.$refs.checkInForm.validate(async valid => {
        if (valid) {
          const res = await checkIn(this.checkInForm.studentId, this.checkInForm.bedId)
          if (res.code === 200) {
            this.$message.success('入住成功')
            this.checkInDialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleCheckOut(row) {
      this.$confirm('确定要办理该学生退宿吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await checkOut(row.id)
        if (res.code === 200) {
          this.$message.success('退宿成功')
          this.loadData()
        }
      }).catch(() => {})
    }
  }
}
</script>
