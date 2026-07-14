import request from '@/utils/request'

export function getOverview() {
  return request({
    url: '/api/statistics/overview',
    method: 'get'
  })
}

export function getBuildingOccupancy() {
  return request({
    url: '/api/statistics/buildingOccupancy',
    method: 'get'
  })
}

export function getRepairTrend(params) {
  return request({
    url: '/api/statistics/repairTrend',
    method: 'get',
    params
  })
}

export function getCollegeDistribution() {
  return request({
    url: '/api/statistics/collegeDistribution',
    method: 'get'
  })
}

export function getRepairType() {
  return request({
    url: '/api/statistics/repairType',
    method: 'get'
  })
}
