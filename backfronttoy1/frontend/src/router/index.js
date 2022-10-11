import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import Router from 'vue-router';
import Main from '@/components/Main';
import IndexPage from '@/components/Index';
import OauthRedirect from '@/components/oauth/Redirect';

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue'),
  },
  {
    path: '/login',
    name: 'login',
    component: () => import(/* webpackChunkName: "login" */ '../views/LoginView.vue'),
  },
  {
    path: '/register',
    name: 'register',
    component: () => import(/* webpackChunkName: "register" */ '../views/RegisterView.vue'),
  },
  {
    path: '/example',
    name: 'example',
    component: () => import(/* webpackChunkName: "register" */ '../views/ExampleView.vue'),
  },
  {
    path: '/oauth/redirect',
    name: 'OauthRedrect',
    component: OauthRedirect
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;