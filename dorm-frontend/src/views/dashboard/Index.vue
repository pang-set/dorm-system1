<template>
  <div class="dashboard-container">
    <div class="welcome-banner">
      <div class="banner-left">
        <h2 class="banner-title">{{ greeting }}，{{ userInfo.realName || userInfo.username }}</h2>
        <p class="banner-subtitle">{{ todayText }}</p>
      </div>
      <div class="banner-right">
        <div class="quick-stats">
          <div class="quick-stat">
            <div class="quick-num">{{ overview.pendingRepairs || 0 }}</div>
            <div class="quick-label">待处理报修</div>
          </div>
          <div class="quick-divider"></div>
          <div class="quick-stat">
            <div class="quick-num">{{ overview.totalStudents || 0 }}</div>
            <div class="quick-label">在校学生</div>
          </div>
          <div class="quick-divider"></div>
          <div class="quick-stat">
            <div class="quick-num">{{ overview.occupancyRate || 0 }}%</div>
            <div class="quick-label">入住率</div>
          </div>
        </div>
      </div>
    </div>

    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="12" :sm="12" :md="6">
        <div class="stat-card stat-card-1 hover-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">学生总数</div>
              <div class="stat-value">{{ overview.totalStudents || 0 }}</div>
              <div class="stat-trend">实时数据</div>
            </div>
            <div class="stat-icon">
              <i class="el-icon-user-solid"></i>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <div class="stat-card stat-card-2 hover-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">楼栋数量</div>
              <div class="stat-value">{{ overview.totalBuildings || 0 }}</div>
              <div class="stat-trend">在管资产</div>
            </div>
            <div class="stat-icon">
              <i class="el-icon-office-building"></i>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <div class="stat-card stat-card-3 hover-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">宿舍入住率</div>
              <div class="stat-value">{{ overview.occupancyRate || 0 }}%</div>
              <div class="stat-trend">资源使用</div>
            </div>
            <div class="stat-icon">
              <i class="el-icon-s-home"></i>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <div class="stat-card stat-card-4 hover-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">待处理报修</div>
              <div class="stat-value">{{ overview.pendingRepairs || 0 }}</div>
              <div class="stat-trend">需关注</div>
            </div>
            <div class="stat-icon">
              <i class="el-icon-s-tools"></i>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card class="quick-actions" shadow="never">
      <div slot="header" class="card-header">
        <span>快捷操作</span>
        <span class="card-tip">点击直达常用功能</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="action in quickActions" :key="action.path">
          <div class="action-item" @click="goTo(action.path)">
            <div class="action-icon" :style="{ background: action.color }">
              <i :class="action.icon"></i>
            </div>
            <div class="action-text">{{ action.title }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-header">
            <span>楼栋入住率</span>
            <span class="card-tip">实时</span>
          </div>
          <div ref="buildingChart" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-header">
            <span>报修趋势（近 7 天）</span>
            <span class="card-tip">单位：条</span>
          </div>
          <div ref="trendChart" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-header">
            <span>学院分布</span>
            <span class="card-tip">按学生人数</span>
          </div>
          <div ref="collegeChart" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-header">
            <span>报修类型分布</span>
            <span class="card-tip">累计</span>
          </div>
          <div ref="repairTypeChart" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="activity-card">
      <div slot="header" class="card-header">
        <span><i class="el-icon-time"></i> 最新动态</span>
        <span class="card-tip">展示最近的业务事件</span>
      </div>
      <el-timeline>
        <el-timeline-item
          v-for="(activity, index) in activities"
          :key="index"
          :type="activity.type"
          :timestamp="activity.time">
          <span class="activity-text">{{ activity.content }}</span>
          <el-tag size="mini" :type="activity.tagType" effect="plain" class="activity-tag">
            {{ activity.tag }}
          </el-tag>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getOverview, getBuildingOccupancy, getRepairTrend, getCollegeDistribution, getRepairType } from '@/api/statistics'

export default {
  name: 'Dashboard',
  data() {
    return {
      overview: {},
      buildingChart: null,
      trendChart: null,
      collegeChart: null,
      repairTypeChart: null,
      quickActions: [
        { title: '登记晚归', path: '/late/list', icon: 'el-icon-time', color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
        { title: '提交报修', path: '/repair/list', icon: 'el-icon-s-tools', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
        { title: '调宿申请', path: '/student/changeRoom', icon: 'el-icon-s-promotion', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
        { title: '请假报备', path: '/late/leave', icon: 'el-icon-document', color: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)' }
      ],
      activities: [
        { time: '', content: '暂无动态数据，请连接后端服务', tag: '提示', tagType: 'info', type: 'info' }
      ]
    }
  },
  computed: {
    userInfo() {
      return this.$store.getters.userInfo
    },
    greeting() {
      const h = new Date().getHours()
      if (h < 12) return '上午好'
      if (h < 18) return '下午好'
      return '晚上好'
    },
    todayText() {
      const now = new Date()
      const week = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][now.getDay()]
      return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 · ${week}`
    }
  },
  mounted() {
    this.loadData()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
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
          getRepairTrend({ days: 7 }),
          getCollegeDistribution(),
          getRepairType()
        ])

        if (overviewRes.code === 200) this.overview = overviewRes.data
        if (buildingRes.code === 200) this.initBuildingChart(buildingRes.data)
        if (trendRes.code === 200) this.initTrendChart(trendRes.data)
        if (collegeRes.code === 200) this.initCollegeChart(collegeRes.data)
        if (repairTypeRes.code === 200) this.initRepairTypeChart(repairTypeRes.data)
      } catch (e) {
        // 图表数据加载失败时静默处理
      }
    },
    handleResize() {
      this.buildingChart && this.buildingChart.resize()
      this.trendChart && this.trendChart.resize()
      this.collegeChart && this.collegeChart.resize()
      this.repairTypeChart && this.repairTypeChart.resize()
    },
    goTo(path) {
      this.$router.push(path)
    },
    initBuildingChart(data) {
      this.buildingChart = echarts.init(this.$refs.buildingChart)
      this.buildingChart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: data.map(d => d.buildingName),
          axisLine: { lineStyle: { color: '#e4e7ed' } },
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: { formatter: '{value}%', color: '#909399' },
          splitLine: { lineStyle: { color: '#f0f2f5' } }
        },
        series: [{
          name: '入住率',
          type: 'bar',
          data: data.map(d => parseFloat(d.occupancyRate)),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ]),
            borderRadius: [6, 6, 0, 0]
          },
          barWidth: '40%',
          label: { show: true, position: 'top', formatter: '{c}%', color: '#606266' }
        }]
      })
    },
    initTrendChart(data) {
      this.trendChart = echarts.init(this.$refs.trendChart)
      this.trendChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: data.dates,
          axisLine: { lineStyle: { color: '#e4e7ed' } },
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: '#909399' },
          splitLine: { lineStyle: { color: '#f0f2f5' } }
        },
        series: [{
          name: '报修数量',
          type: 'line',
          data: data.counts,
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: { color: '#43e97b', width: 3 },
          itemStyle: { color: '#43e97b' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(67, 233, 123, 0.4)' },
              { offset: 1, color: 'rgba(67, 233, 123, 0.05)' }
            ])
          }
        }]
      })
    },
    initCollegeChart(data) {
      this.collegeChart = echarts.init(this.$refs.collegeChart)
      this.collegeChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} 人 ({d}%)' },
        legend: { orient: 'vertical', left: 'left', textStyle: { color: '#606266' } },
        color: ['#667eea', '#f5576c', '#4facfe', '#43e97b', '#fa709a', '#a18cd1', '#fee140', '#38f9d7'],
        series: [{
          name: '学生人数',
          type: 'pie',
          radius: ['45%', '70%'],
          center: ['60%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: { show: true, formatter: '{b}\n{d}%', color: '#606266' },
          labelLine: { show: true },
          data: data
        }]
      })
    },
    initRepairTypeChart(data) {
      this.repairTypeChart = echarts.init(this.$refs.repairTypeChart)
      this.repairTypeChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} 条 ({d}%)' },
        legend: { bottom: 0, textStyle: { color: '#606266' } },
        color: ['#667eea', '#f5576c', '#4facfe', '#43e97b', '#fa709a', '#a18cd1'],
        series: [{
          name: '报修数量',
          type: 'pie',
          radius: '60%',
          center: ['50%', '45%'],
          data: data,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.3)'
            }
          }
        }]
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.dashboard-container {
  padding: $spacing-lg;
  background: $bg-page;
  min-height: calc(100vh - 60px);
}


.welcome-banner {
  background: $blue-gradient;
  border-radius: $radius-xl;
  padding: 28px 32px;
  margin-bottom: $spacing-lg;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.25);
  position: relative;
  overflow: hidden;

  &::before, &::after {
    content: '';
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
  }

  &::before {
    width: 200px;
    height: 200px;
    right: -60px;
    top: -80px;
  }

  &::after {
    width: 120px;
    height: 120px;
    right: 100px;
    bottom: -40px;
  }

  .banner-left {
    position: relative;
    z-index: 1;
  }

  .banner-title {
    font-size: 24px;
    font-weight: 700;
    margin: 0 0 6px 0;
  }

  .banner-subtitle {
    font-size: $font-size-base;
    opacity: 0.9;
    margin: 0;
  }

  .banner-right {
    position: relative;
    z-index: 1;
  }

  .quick-stats {
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    border-radius: $radius-lg;
    padding: 14px 24px;
    gap: 20px;
  }

  .quick-stat {
    text-align: center;
    color: #fff;

    .quick-num {
      font-size: 22px;
      font-weight: 700;
    }

    .quick-label {
      font-size: $font-size-xs;
      opacity: 0.85;
      margin-top: 2px;
    }
  }

  .quick-divider {
    width: 1px;
    height: 32px;
    background: rgba(255, 255, 255, 0.3);
  }
}


.stat-cards {
  margin-bottom: $spacing-lg;

  .stat-card {
    border-radius: $radius-lg;
    padding: 24px;
    color: #fff;
    position: relative;
    overflow: hidden;
    margin-bottom: $spacing-md;
    min-height: 120px;

    &::after {
      content: '';
      position: absolute;
      right: -30px;
      top: -30px;
      width: 120px;
      height: 120px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
    }
  }

  .stat-card-1 { background: $blue-gradient; }
  .stat-card-2 { background: $pink-gradient; }
  .stat-card-3 { background: $cyan-gradient; }
  .stat-card-4 { background: $green-gradient; }

  .stat-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 1;
  }

  .stat-info {
    .stat-label {
      font-size: $font-size-base;
      opacity: 0.9;
    }

    .stat-value {
      font-size: 32px;
      font-weight: 700;
      margin: 4px 0 6px;
    }

    .stat-trend {
      font-size: $font-size-xs;
      opacity: 0.75;
      background: rgba(255, 255, 255, 0.18);
      display: inline-block;
      padding: 2px 8px;
      border-radius: $radius-sm;
    }
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: $radius-lg;
    background: rgba(255, 255, 255, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;

    i {
      font-size: 28px;
      color: #fff;
    }
  }
}


::v-deep .el-card {
  margin-bottom: $spacing-lg;
  border: none;

  .el-card__header {
    padding: 16px 20px;
    background: #fff;
    border-bottom: 1px solid $border-lighter;
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  color: $text-primary;
  font-size: $font-size-md;

  .card-tip {
    font-size: $font-size-xs;
    color: $text-secondary;
    font-weight: normal;
  }
}


.quick-actions {
  .action-item {
    text-align: center;
    padding: 20px 12px;
    border-radius: $radius-lg;
    cursor: pointer;
    transition: all $transition-base;
    margin-bottom: $spacing-md;

    &:hover {
      background: $bg-light;
      transform: translateY(-2px);
    }

    .action-icon {
      width: 56px;
      height: 56px;
      border-radius: $radius-lg;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 10px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

      i {
        font-size: 26px;
        color: #fff;
      }
    }

    .action-text {
      font-size: $font-size-base;
      color: $text-primary;
      font-weight: 500;
    }
  }
}


.chart-row {
  .chart-card {
    .chart-box {
      height: 320px;
    }
  }
}


.activity-card {
  ::v-deep .el-timeline-item__node {
    background: $primary-color;
  }

  .activity-text {
    color: $text-primary;
    margin-right: $spacing-sm;
  }

  .activity-tag {
    vertical-align: middle;
  }
}
</style>
