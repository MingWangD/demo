import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    {
      path: '/manager',
      component: () => import('@/views/Manager.vue'),
      redirect: '/manager/home',
      children: [
        { path: 'home', component: () => import('@/views/manager/Home.vue')},
        { path: 'admin', component: () => import('@/views/manager/Admin.vue')},
        { path: 'student-overview', component: () => import('@/views/student/Overview.vue')},
        { path: 'student-homework', component: () => import('@/views/student/Homework.vue')},
        { path: 'student-exam', component: () => import('@/views/student/Exam.vue')},
        { path: 'student-risk', component: () => import('@/views/student/Risk.vue')},
        { path: 'teacher-dashboard', component: () => import('@/views/teacher/Dashboard.vue')},
        { path: 'teacher-homework', component: () => import('@/views/teacher/HomeworkManage.vue')},
        { path: 'teacher-exam', component: () => import('@/views/teacher/ExamManage.vue')},
        { path: 'teacher-high-risk', component: () => import('@/views/teacher/HighRisk.vue')},
        { path: 'teacher-student-detail', component: () => import('@/views/teacher/StudentDetail.vue')},
        { path: 'teacher-intervention', component: () => import('@/views/teacher/Intervention.vue')},
      ]
    },
    { path: '/login', component: () => import('@/views/Login.vue') }
  ]
})

export default router
