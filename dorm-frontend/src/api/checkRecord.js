import request from '@/utils/request'

export function getCheckRecordList(params) {
  return request({
    url: '/api/checkRecord/page',
    method: 'get',
    params
  })
}

export function getCheckRecord(id) {
  return request({
    url: `/api/checkRecord/${id}`,
    method: 'get'
  })
}

export function addCheckRecord(data) {
  return request({
    url: '/api/checkRecord',
    method: 'post',
    data
  })
}

export function updateCheckRecord(data) {
  return request({
    url: '/api/checkRecord',
    method: 'put',
    data
  })
}

export function deleteCheckRecord(id) {
  return request({
    url: `/api/checkRecord/${id}`,
    method: 'delete'
  })
}
