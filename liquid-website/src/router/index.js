import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/F1index.vue'
import NProgress from "nprogress";
import "nprogress/nprogress.css";

NProgress.configure({
  showSpinner: false,
});

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../pages/login/F1index.vue'),
    meta: {
      title: 'Login'
    }
  },
  {
    path: '',
    name: 'layout',
    component: Layout,
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('../pages/home/F1index')
      },
      {
        path: 'new',
        name: 'new',
        component: () => import('../pages/new/F1index')
      },
      {
        path: 'search',
        name: 'search',
        component: () => import('../pages/search/index'),
      },
      {
        path: '/:user',
        name: 'user',
        component: () => import('../pages/user/F1index.vue')
      },
      {
        path: '/:user/:rep',
        name: 'rep',
        component: () => import('../pages/rep/F1index.vue'),
        children: [
          {
            path: 'graph/:type?',
            name: 'insight',
            component: () => import('../pages/rep/graph/F1index'),
          },
          {
            path: 'fork',
            name: 'fork',
            component: () => import('../pages/rep/fork/F1index')
          },
          {
            path: 'issue',
            name: 'issue',
            component: () => import('../pages/rep/issue/F1index')
          },
          {
            path: 'issue/new/choose',
            name: 'issueNewChoose',
            component: () => import('../pages/rep/issueNew/F1index')
          },
          {
            path: 'issue/new',
            name: 'issueNewDetail',
            component: () => import('../pages/rep/issueNewDetail/F1index')
          },
          {
            path: 'issue/:index',
            name: 'issueDetail',
            component: () => import('../pages/rep/issueDetail/F1index')
          },
          {
            path: 'pull',
            name: 'pull',
            component: () => import('../pages/rep/pull/F1index'),
          },
          {
            path: 'pull/:index',
            name: 'pullDetail',
            component: () => import('../pages/rep/pullDetail'),
          },
          {
            path: 'compare',
            name: 'pullNew',
            component: () => import('../pages/rep/pullNew/F1index')
          },
          {
            path: 'setting',
            name: 'setting',
            meta: {
              title: 'Settings',
            },
            component: () => import('../pages/rep/setting/F1index')
          },
          {
            path: 'new/:branch?/:dir*',
            name: 'newFile',
            component: () => import('../pages/rep/new/F1index')
          },
          {
            path: 'upload/:branch?/:dir*',
            name: 'upload',
            component: () => import('../pages/rep/upload/F1index')
          },
          {
            path: 'tree/:branch?/:dir*',
            name: 'tree',
            component: () => import('../pages/rep/tree/F1index')
          },
          {
            path: 'blob/:branch?/:dir*',
            name: 'blob',
            component: () => import('../pages/rep/blob/F1index')
          },
          {
            path: 'commit/:branch?',
            name: 'commit',
            component: () => import('../pages/rep/commit/F1index')
          },
          {
            path: 'commit/:branch/:SHA',
            name: 'commitDetail',
            component: () => import('../pages/rep/commitDetail/F1index')
          },
          {
            path: ':branch?/:dir*',
            name: 'code',
            component: () => import('../pages/rep/code/F1index'),
          },

        ]
      },
      {
        path: ':pathMatch(.*)*',
        name: 'notFound',
        component: () => import('../pages/error/F1index.vue')
      }
    ]
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    return { x: 0, y: 0 };
  },
  routes: routes,
})

router.beforeEach((to, from, next) => {
  NProgress.start();
  next();
})

router.afterEach(() => {
  NProgress.done();
});

export default router
