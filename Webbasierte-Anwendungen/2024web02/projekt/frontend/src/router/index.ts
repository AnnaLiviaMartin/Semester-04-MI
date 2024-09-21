import { createRouter, createWebHistory } from 'vue-router'
import TourenListeView from "@/views/TourenListeView.vue";
import TourView from "@/views/TourView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/touren', component: TourenListeView},
    { path: '/tour/:tourid', component: TourView, props: true },
    { path: '', redirect: (to: any) => {
      const { hash, params, query } = to
      return '/touren';
      }}
  ]
})

export default router
