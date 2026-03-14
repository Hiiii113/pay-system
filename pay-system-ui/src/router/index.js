import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            alias: '/main',
            name: 'main',
            component: () => import('../views/Main.vue')
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/Login.vue')
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('../views/Register.vue')
        }
    ]
})

export default router
