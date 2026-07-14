<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">宿舍管理</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="楼栋">
        <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
          <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="宿舍号">
        <el-input v-model="searchForm.roomNo" placeholder="请输入宿舍号" clearable></el-input>
      </el-form-item>
      <el-form-item label="楼层">
        <el-input v-model="searchForm.floor" placeholder="请输入楼层" clearable></el-input>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="正常" :value="1"></el-option>
          <el-option label="停用" :value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增宿舍</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading" @expand-change="handleExpand">
      <el-table-column type="expand">
        <template slot-scope="props">
          <div style="padding: 10px;">
            <h4 style="margin-bottom: 10px;">床位列表</h4>
            <el-table :data="props.row.bedList" border size="small" v-loading="props.row.bedLoading">
              <el-table-column prop="bedNo" label="床位号" width="120"></el-table-column>
              <el-table-column prop="status" label="状态" width="120">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === 0 ? 'success' : 'warning'" size="small">
                    {{ scope.row.status === 0 ? '空闲' : '已入住' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="studentId" label="学生ID" width="120"></el-table-column>
              <el-table-column prop="studentName" label="学生姓名"></el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="roomNo" label="宿舍号" width="120"></el-table-column>
      <el-table-column prop="floor" label="楼层" width="80"></el-table-column>
      <el-table-column prop="buildingId" label="楼栋ID" width="100"></el-table-column>
      <el-table-column prop="bedTotal" label="总床位" width="80"></el-table-column>
      <el-table-column prop="bedUsed" label="已用" width="80"></el-table-column>
      <el-table-column prop="roomType" label="房间类型" width="120">
        <template slot-scope="scope">
          {{ getRoomTypeText(scope.row.roomType) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
            {{ scope.row.status === 1 ? '正常' : '停用' }}
          </el-tag>
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
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍号" prop="roomNo">
          <el-input v-model="form.roomNo"></el-input>
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="1" :max="50"></el-input-number>
        </el-form-item>
        <el-form-item label="总床位" prop="bedTotal">
          <el-input-number v-model="form.bedTotal" :min="1" :max="20"></el-input-number>
        </el-form-item>
        <el-form-item label="房间类型" prop="roomType">
          <el-select v-model="form.roomType" style="width: 100%">
            <el-option label="标准间" value="标准间"></el-option>
            <el-option label="四人间" value="四人间"></el-option>
            <el-option label="六人间" value="六人间"></el-option>
            <el-option label="八人间" value="八人间"></el-option>
          </el-select>
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
import { getRoomList, addRoom, updateRoom, deleteRoom, getRoomBeds } from '@/api/room'
import { getAllBuildings } from '@/api/building'

export default {
  name: 'Room',
  data() {
    return {
      searchForm: {
        buildingId: '',
        roomNo: '',
        floor: '',
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
      form: {
        id: null,
        buildingId: '',
        roomNo: '',
        floor: 1,
        bedTotal: 6,
        roomType: '六人间',
        remark: ''
      },
      rules: {
        buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
        roomNo: [{ required: true, message: '请输入宿舍号', trigger: 'blur' }],
        floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
        bedTotal: [{ required: true, message: '请输入总床位数', trigger: 'blur' }],
        roomType: [{ required: true, message: '请选择房间类型', trigger: 'change' }]
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
    getRoomTypeText(type) {
      const map = { '标准间': '标准间', '四人间': '四人间', '六人间': '六人间', '八人间': '八人间' }
      return map[type] || type
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getRoomList({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          ...this.searchForm
        })
        if (res.code === 200) {
          this.tableData = res.data.records.map(item => ({
            ...item,
            bedList: [],
            bedLoading: false
          }))
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
      this.searchForm = { buildingId: '', roomNo: '', floor: '', status: '' }
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
      this.dialogTitle = '新增宿舍'
      this.form = {
        id: null,
        buildingId: '',
        roomNo: '',
        floor: 1,
        bedTotal: 6,
        roomType: '六人间',
        remark: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑宿舍'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const res = this.isEdit
            ? await updateRoom(this.form)
            : await addRoom(this.form)

          if (res.code === 200) {
            this.$message.success(this.isEdit ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该宿舍吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteRoom(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    },
    handleExpand(row, expandedRows) {
      if (expandedRows.includes(row)) {
        this.loadRoomBeds(row)
      }
    },
    async loadRoomBeds(row) {
      if (row.bedList && row.bedList.length > 0) return
      this.$set(row, 'bedLoading', true)
      try {
        const res = await getRoomBeds(row.id)
        if (res.code === 200) {
          this.$set(row, 'bedList', res.data)
        }
      } finally {
        this.$set(row, 'bedLoading', false)
      }
    }
  }
}
</script>
