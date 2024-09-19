import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Home from '../views/Home.vue'
import About from "@/views/About.vue";
import AdminEbooks from "@/views/admin/Admin-ebooks.vue";
import AdminCategory from "@/views/admin/Admin-categorys.vue";
import AdminDocs from "@/views/admin/Admin-docs.vue";
import Doc from "@/views/Doc.vue"

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/about',
    name: 'About',
    component: About
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
  },
  {
    path: '/admin/ebook',
    name: 'AdminEbooks',
    component: AdminEbooks
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
  },
  {
    path: '/admin/category',
    name: 'AdminCategory',
    component: AdminCategory
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
  },
  {
    path: '/admin/doc',
    name: 'AdminDoc',
    component: AdminDocs
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
  },
  {
    path: '/doc',
    name: 'Doc',
    component: Doc
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
