<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">统计报表</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header">
            <span>学生总数</span>
            <span style="float:right; color:#409EFF; font-size:24px; font-weight:bold;">
              {{ overview.totalStudents || 0 }}
            </span>
          </div>
          <div style="padding: 10px 0;">
            <el-progress :percentage="studentInRate" :status="'success'"></el-progress>
            <p style="color: #909399; font-size: 12px; margin-top: 10px;">
              已入住 {{ overview.checkedInStudents || 0 }} 人
            </p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header">
            <span>床位利用率</span>
            <span style="float:right; color:#67c23a; font-size:24px; font-weight:bold;">
              {{ overview.occupancyRate || 0 }}%
            </span>
          </div>
          <div style="padding: 10px 0;">
            <el-progress :percentage="parseFloat(overview.occupancyRate || 0)" :status="'success'"></el-progress>
            <p style="color: #909399; font-size: 12px; margin-top: 10px;">
              共 {{ overview.totalBeds || 0 }} 个床位，已入住 {{ overview.occupiedBeds || 0 }} 个
            </p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header">
            <span>报修工单</span>
            <span style="float:right; color:#e6a23c; font-size:24px; font-weight:bold;">
              {{ overview.pendingRepairs || 0 }}
            </span>
          </div>
          <div style="padding: 10px 0;">
            <p style="color: #606266;">
              待处理: <el-tag size="small" type="warning">{{ overview.pendingRepairs || 0 }}</el-tag>
              处理中: <el-tag size="small" type="primary">{{ overview.processingRepairs || 0 }}</el-tag>
              已完成: <el-tag size="small" type="success">{{ overview.completedRepairs || 0 }}</el-tag>
            </p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>各楼栋入住率</span>
          </div>
          <div ref="buildingChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>报修趋势（近30天）</span>
          </div>
          <div ref="trendChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>学院学生分布</span>
          </div>
          <div ref="collegeChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>报修类型分布</span>
          </div>
          <div ref="repairTypeChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getOverview, getBuildingOccupancy, getRepairTrend, getCollegeDistribution, getRepairType } from '@/api/statistics'

export default {
  name: 'Statistics',
  data() {
    return {
      overview: {},
      studentInRate: 0,
      buildingChart: null,
      trendChart: null,
      collegeChart: null,
      repairTypeChart: null
    }
  },
  mounted() {
    this.loadData()
  },
  beforeDestroy() {
    if (this.buildingChart) this.buildingChart.dispose()
    if (this.trendChart) this.trendChart.dispose()
    if (this.collegeChart) this.collegeChart.dispose()
    if (this.repairTypeChart) this.repairTypeChart.dispose()
  },
  methods: {
    async loadData() {
      try {
        const [overviewRes, buildingRes, trendRes, collegeRes, repairTypeRes] = await Promise.all([
          getOverview(),
          getBuildingOccupancy(),
          getRepairTrend({ days: 30 }),
          getCollegeDistribution(),
          getRepairType()
        ])

        if (overviewRes.code === 200) {
          this.overview = overviewRes.data
          const total = overviewRes.data.totalStudents || 1
          const checked = overviewRes.data.checkedInStudents || 0
          this.studentInRate = Math.round(checked * 100 / total)
        }
        if (buildingRes.code === 200) {
          this.initBuildingChart(buildingRes.data)
        }
        if (trendRes.code === 200) {
          this.initTrendChart(trendRes.data)
        }
        if (collegeRes.code === 200) {
          this.initCollegeChart(collegeRes.data)
        }
        if (repairTypeRes.code === 200) {
          this.initRepairTypeChart(repairTypeRes.data)
        }
      } catch (e) {
        console.error(e)
      }
    },
    initBuildingChart(data) {
      this.buildingChart = echarts.init(this.$refs.buildingChart)
      this.buildingChart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: data.map(d => d.buildingName) },
        yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
        series: [{
          name: '入住率',
          type: 'bar',
          data: data.map(d => parseFloat(d.occupancyRate)),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ])
          },
          barWidth: '40%'
        }]
      })
    },
    initTrendChart(data) {
      this.trendChart = echarts.init(this.$refs.trendChart)
      this.trendChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: data.dates, boundaryGap: false },
        yAxis: { type: 'value' },
        series: [{
          name: '报修数量',
          type: 'line',
          data: data.counts,
          smooth: true,
          itemStyle: { color: '#67C23A' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(103, 194, 58, 0.5)' },
              { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
            ])
          }
        }]
      })
    },
    initCollegeChart(data) {
      this.collegeChart = echarts.init(this.$refs.collegeChart)
      this.collegeChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
        legend: { orient: 'vertical', left: 'left', top: 'center' },
        series: [{
          name: '学生人数',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['60%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: {
            label: { show: true, fontSize: '14', fontWeight: 'bold' }
          },
          data: data
        }]
      })
    },
    initRepairTypeChart(data) {
      this.repairTypeChart = echarts.init(this.$refs.repairTypeChart)
      this.repairTypeChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}个 ({d}%)' },
        legend: { orient: 'vertical', left: 'left', top: 'center' },
        series: [{
          name: '报修数量',
          type: 'pie',
          radius: '60%',
          center: ['60%', '50%'],
          data: data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
    }
  }
}
</script>
