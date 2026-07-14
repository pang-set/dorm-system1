import request from '@/utils/request'

export function getBedList(params) {
  return request({
    url: '/api/bed/page',
    method: 'get',
    params
  })
}

export function getBed(id) {
  return request({
    url: `/api/bed/${id}`,
    method: 'get'
  })
}

export function addBed(data) {
  return request({
    url: '/api/bed',
    method: 'post',
    data
  })
}

export function updateBed(data) {
  return request({
    url: '/api/bed',
    method: 'put',
    data
  })
}

export function deleteBed(id) {
  return request({
    url: `/api/bed/${id}`,
    method: 'delete'
  })
}
