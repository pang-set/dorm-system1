<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">楼栋管理</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="楼栋名称">
        <el-input v-model="searchForm.buildingName" placeholder="请输入楼栋名称" clearable></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="searchForm.gender" placeholder="请选择" clearable>
          <el-option label="男寝" value="male"></el-option>
          <el-option label="女寝" value="female"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增楼栋</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="buildingNo" label="楼栋编号" width="120"></el-table-column>
      <el-table-column prop="buildingName" label="楼栋名称" width="150"></el-table-column>
      <el-table-column prop="gender" label="性别" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.gender === 'male' ? 'primary' : 'danger'" size="small">
            {{ scope.row.gender === 'male' ? '男寝' : '女寝' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="floorCount" label="楼层数" width="80"></el-table-column>
      <el-table-column prop="roomCount" label="房间数" width="80"></el-table-column>
      <el-table-column prop="bedCount" label="床位数" width="80"></el-table-column>
      <el-table-column prop="occupiedCount" label="已入住" width="80"></el-table-column>
      <el-table-column prop="remark" label="备注"></el-table-column>
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
        <el-form-item label="楼栋编号" prop="buildingNo">
          <el-input v-model="form.buildingNo"></el-input>
        </el-form-item>
        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input v-model="form.buildingName"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" style="width: 100%">
            <el-option label="男寝" value="male"></el-option>
            <el-option label="女寝" value="female"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="楼层数" prop="floorCount">
          <el-input-number v-model="form.floorCount" :min="1" :max="20"></el-input-number>
        </el-form-item>
        <el-form-item label="每楼房间数">
          <el-input-number v-model="roomsPerFloor" :min="1" :max="50"></el-input-number>
          <span style="margin-left: 10px; color: #909399; font-size: 12px;">(自动计算总房间数)</span>
        </el-form-item>
        <el-form-item label="每间床位数">
          <el-input-number v-model="bedsPerRoom" :min="1" :max="10"></el-input-number>
          <span style="margin-left: 10px; color: #909399; font-size: 12px;">(自动计算总床位数)</span>
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
import { getBuildingList, addBuilding, updateBuilding, deleteBuilding } from '@/api/building'

export default {
  name: 'Building',
  data() {
    return {
      searchForm: {
        buildingName: '',
        gender: ''
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      roomsPerFloor: 20,
      bedsPerRoom: 6,
      form: {
        id: null,
        buildingNo: '',
        buildingName: '',
        gender: 'male',
        floorCount: 6,
        roomCount: 0,
        bedCount: 0,
        occupiedCount: 0,
        remark: ''
      },
      rules: {
        buildingNo: [{ required: true, message: '请输入楼栋编号', trigger: 'blur' }],
        buildingName: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getBuildingList({
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
      this.searchForm = { buildingName: '', gender: '' }
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
      this.dialogTitle = '新增楼栋'
      this.form = {
        id: null,
        buildingNo: '',
        buildingName: '',
        gender: 'male',
        floorCount: 6,
        roomCount: 0,
        bedCount: 0,
        occupiedCount: 0,
        remark: ''
      }
      this.roomsPerFloor = 20
      this.bedsPerRoom = 6
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑楼栋'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          if (!this.isEdit) {
            this.form.roomCount = this.form.floorCount * this.roomsPerFloor
            this.form.bedCount = this.form.roomCount * this.bedsPerRoom
          }

          const res = this.isEdit
            ? await updateBuilding(this.form)
            : await addBuilding(this.form)

          if (res.code === 200) {
            this.$message.success(this.isEdit ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.loadData()
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该楼栋吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteBuilding(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      }).catch(() => {})
    }
  }
}
</script>
