import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import CocktailCreateView from '../views/CocktailCreateView.vue'
import CarteView from '../views/CarteView.vue'
import CocktailDetails from '../views/CocktailDetails.vue'
import CartView from '../views/PanierView.vue'
import OrdersView from '../views/OrdersView.vue'
import MesCommandes from '../views/MesCommandes.vue'

import { getUserRole } from '../utils/auth' // ton helper

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: LoginView },
  { path: '/register', name: 'Register', component: RegisterView },
  { path: '/carte', name: 'Carte', component: CarteView },
  { path: '/cocktails/:id', name: 'CocktailDetails', component: CocktailDetails, props: true },
  {
    path: '/cocktails/create',
    name: 'CocktailCreate',
    component: CocktailCreateView,
    meta: { requiresRole: 'BARMAKER' }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: OrdersView,
    meta: { requiresRole: 'BARMAKER' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: CartView,
    meta: { requiresRole: 'CLIENT' }
  },
  {
    path: '/mes-commandes',
    name: 'MesCommandes',
    component: MesCommandes,
    meta: { requiresRole: 'CLIENT' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 🛡️ Garde globale pour la protection par rôle
router.beforeEach((to, from, next) => {
  const requiredRole = to.meta.requiresRole
  if (!requiredRole) return next() // pas de restriction

  const userRole = getUserRole()
  if (userRole === requiredRole) {
    return next()
  } else {
    console.warn(`Accès refusé à ${to.fullPath} pour le rôle ${userRole}`)
    return next('/login') // ou vers une page 403 ?
  }
})

export default router
