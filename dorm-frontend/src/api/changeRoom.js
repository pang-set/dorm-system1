import request from '@/utils/request'

export function getChangeRoomList(params) {
  return request({
    url: '/api/changeRoom/page',
    method: 'get',
    params
  })
}

export function addChangeRoom(data) {
  return request({
    url: '/api/changeRoom',
    method: 'post',
    data
  })
}

export function deleteChangeRoom(id) {
  return request({
    url: `/api/changeRoom/${id}`,
    method: 'delete'
  })
}

export function approveChangeRoom(id, data) {
  return request({
    url: `/api/changeRoom/approve/${id}`,
    method: 'put',
    data
  })
}
