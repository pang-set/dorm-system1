<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">卫生检查</h2>
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
      <el-form-item label="开始日期">
        <el-date-picker
          v-model="searchForm.startDate"
          type="date"
          placeholder="选择日期"
          value-format="yyyy-MM-dd"
          clearable>
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束日期">
        <el-date-picker
          v-model="searchForm.endDate"
          type="date"
          placeholder="选择日期"
          value-format="yyyy-MM-dd"
          clearable>
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增检查</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column label="楼栋" width="120">
        <template slot-scope="scope">
          {{ getBuildingName(scope.row.buildingId) }}
        </template>
      </el-table-column>
      <el-table-column prop="roomNo" label="宿舍号" width="120"></el-table-column>
      <el-table-column prop="inspectorName" label="检查人" width="120"></el-table-column>
      <el-table-column prop="checkTime" label="检查时间" width="180"></el-table-column>
      <el-table-column prop="hygieneScore" label="卫生分数" width="100">
        <template slot-scope="scope">
          <span :style="{ color: getScoreColor(scope.row.hygieneScore), fontWeight: 'bold' }">
            {{ scope.row.hygieneScore }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="hygieneLevel" label="卫生等级" width="100">
        <template slot-scope="scope">
          <el-tag :type="getLevelTagType(scope.row.hygieneLevel)" size="small">
            {{ scope.row.hygieneLevel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150"></el-table-column>
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
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%" @change="handleFormBuildingChange">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请选择宿舍" style="width: 100%">
            <el-option v-for="item in formRoomList" :key="item.id" :label="item.roomNo" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="卫生分数" prop="hygieneScore">
          <el-input-number v-model="form.hygieneScore" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="卫生等级" prop="hygieneLevel">
          <el-select v-model="form.hygieneLevel" style="width: 100%">
            <el-option label="优秀" value="优秀"></el-option>
            <el-option label="良好" value="良好"></el-option>
            <el-option label="合格" value="合格"></el-option>
            <el-option label="不合格" value="不合格"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="检查人" prop="inspectorName">
          <el-input v-model="form.inspectorName"></el-input>
        </el-form-item>
        <el-form-item label="检查时间" prop="checkTime">
          <el-date-picker
            v-model="form.checkTime"
            type="datetime"
            placeholder="选择日期时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" :rows="3"></el-input>
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
import { getCheckRecordList, addCheckRecord, updateCheckRecord, deleteCheckRecord } from '@/api/checkRecord'
import { getAllBuildings } from '@/api/building'
import { getRoomList } from '@/api/room'

export default {
  name: 'Check',
  data() {
    return {
      searchForm: {
        buildingId: '',
        roomId: '',
        startDate: '',
        endDate: ''
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      buildingList: [],
      roomList: [],
      formRoomList: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: {
        id: null,
        buildingId: '',
        roomId: '',
        hygieneScore: 90,
        hygieneLevel: '良好',
        inspectorName: '',
        checkTime: '',
        remark: ''
      },
      rules: {
        buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
        roomId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
        hygieneScore: [{ required: true, message: '请输入卫生分数', trigger: 'blur' }],
        hygieneLevel: [{ required: true, message: '请选择卫生等级', trigger: 'change' }],
        inspectorName: [{ required: true, message: '请输入检查人', trigger: 'blur' }],
        checkTime: [{ required: true, message: '请选择检查时间', trigger: 'change' }]
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
    async loadRooms(buildingId, type = 'search') {
      if (!buildingId) {
        if (type === 'search') {
          this.roomList = []
        } else {
          this.formRoomList = []
        }
        return
      }
      const res = await getRoomList({ buildingId, pageNum: 1, pageSize: 1000 })
      if (res.code === 200) {
        if (type === 'search') {
          this.roomList = res.data.records
        } else {
          this.formRoomList = res.data.records
        }
      }
    },
    handleBuildingChange(val) {
      this.searchForm.roomId = ''
      this.loadRooms(val, 'search')
    },
    handleFormBuildingChange(val) {
      this.form.roomId = ''
      this.loadRooms(val, 'form')
    },
    getScoreColor(score) {
      if (score >= 90) return '#67c23a'
      if (score >= 80) return '#409eff'
      if (score >= 60) return '#e6a23c'
      return '#f56c6c'
    },
    getLevelTagType(level) {
      const map = { '优秀': 'success', '良好': 'primary', '合格': 'warning', '不合格': 'danger' }
      return map[level] || 'info'
    },
    getBuildingName(buildingId) {
      const b = this.buildingList.find(item => item.id === buildingId)
      return b ? b.buildingName : (buildingId || '')
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getCheckRecordList({
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
      this.searchForm = { buildingId: '', roomId: '', startDate: '', endDate: '' }
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
      this.dialogTitle = '新增卫生检查'
      this.form = {
        id: null,
        buildingId: '',
        roomId: '',
        hygieneScore: 90,
        hygieneLevel: '良好',
        inspectorName: '',
        checkTime: '',
        remark: ''
      }
      this.formRoomList = []
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑卫生检查'
      this.form = { ...row }
      if (row.buildingId) {
        this.loadRooms(row.buildingId, 'form')
      }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = this.isEdit
            ? await updateCheckRecord(this.form)
            : await addCheckRecord(this.form)

          if (res.code === 200) {
            this.$message.success(this.isEdit ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该检查记录吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteCheckRecord(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    }
  }
}
</script>
