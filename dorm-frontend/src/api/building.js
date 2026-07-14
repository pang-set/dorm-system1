import request from '@/utils/request'

export function getBuildingList(params) {
  return request({
    url: '/api/building/page',
    method: 'get',
    params
  })
}

export function getAllBuildings() {
  return request({
    url: '/api/building/all',
    method: 'get'
  })
}

export function getBuilding(id) {
  return request({
    url: `/api/building/${id}`,
    method: 'get'
  })
}

export function addBuilding(data) {
  return request({
    url: '/api/building',
    method: 'post',
    data
  })
}

export function updateBuilding(data) {
  return request({
    url: '/api/building',
    method: 'put',
    data
  })
}

export function deleteBuilding(id) {
  return request({
    url: `/api/building/${id}`,
    method: 'delete'
  })
}
