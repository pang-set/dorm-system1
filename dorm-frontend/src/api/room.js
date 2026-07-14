import request from '@/utils/request'

export function getRoomList(params) {
  return request({
    url: '/api/room/page',
    method: 'get',
    params
  })
}

export function getRoom(id) {
  return request({
    url: `/api/room/${id}`,
    method: 'get'
  })
}

export function getRoomBeds(id) {
  return request({
    url: `/api/room/${id}/beds`,
    method: 'get'
  })
}

export function addRoom(data) {
  return request({
    url: '/api/room',
    method: 'post',
    data
  })
}

export function updateRoom(data) {
  return request({
    url: '/api/room',
    method: 'put',
    data
  })
}

export function deleteRoom(id) {
  return request({
    url: `/api/room/${id}`,
    method: 'delete'
  })
}

export function getAvailableRooms(params) {
  return request({
    url: '/api/room/available',
    method: 'get',
    params
  })
}
