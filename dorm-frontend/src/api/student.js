import request from '@/utils/request'

export function getStudentList(params) {
  return request({
    url: '/api/student/page',
    method: 'get',
    params
  })
}

export function getStudent(id) {
  return request({
    url: `/api/student/${id}`,
    method: 'get'
  })
}

export function getStudentByNo(studentNo) {
  return request({
    url: `/api/student/no/${studentNo}`,
    method: 'get'
  })
}

export function addStudent(data) {
  return request({
    url: '/api/student',
    method: 'post',
    data
  })
}

export function updateStudent(data) {
  return request({
    url: '/api/student',
    method: 'put',
    data
  })
}

export function deleteStudent(id) {
  return request({
    url: `/api/student/${id}`,
    method: 'delete'
  })
}

export function checkIn(studentId, bedId) {
  return request({
    url: '/api/student/checkIn',
    method: 'post',
    data: { studentId, bedId }
  })
}

export function checkOut(studentId) {
  return request({
    url: '/api/student/checkOut',
    method: 'post',
    data: { studentId }
  })
}
