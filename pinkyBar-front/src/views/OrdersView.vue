<template>
  <div class="cart-view">
    <h1>Liste des commandes</h1>
	<button @click="$router.back()" class="back-btn">← Retour</button>

    <table v-if="orders.length" class="cart-table">
      <thead>
        <tr>
          <th>Numéro Commande</th>
          <th>Date</th>
          <th>Statut commande</th>
          <th>Articles</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.id">
          <td>{{ order.id }}</td>
          <td>{{ formatDate(order.createdAt) }}</td>
          <td>{{ order.status }}</td> <!-- Statut global ici -->
          <td>
            <ul class="items-list">
              <li v-for="item in order.items" :key="item.id">
                {{ item.quantity }} × {{ item.cocktailName }} ({{ item.size }})
              </li>
            </ul>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-else class="empty">Chargement des commandes...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { getAuthHeaders, getUserInfoFromToken } from '../utils/auth'

const orders = ref([])

function formatDate(dateString) {
  return new Date(dateString).toLocaleString()
}

onMounted(async () => {
  try {
    const user = getUserInfoFromToken()
    const response = await axios.get(`/api/orders`, {
      headers: getAuthHeaders()
    })
    orders.value = response.data
  } catch (error) {
    console.error('Erreur lors de la récupération des commandes:', error)
  }
})
</script>

<style scoped>
.confirmation-message {
  background-color: #b46885;
  color: white;
  padding: 1rem 1.5rem;
  border-radius: 10px;
  margin-bottom: 1.5rem;
  font-weight: 600;
  text-align: center;
  font-family: 'Poppins', sans-serif;
}

.cart-view {
  max-width: 800px;
  margin: 2rem auto;
  font-family: 'Poppins', sans-serif;
  color: #5a3e4d;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
}

.empty {
  text-align: center;
  color: #a18a99;
  font-size: 1.2rem;
}

.cart-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1.5rem;
}

.cart-table th,
.cart-table td {
  border: 1px solid #ddd;
  padding: 0.75rem;
  text-align: center;
  vertical-align: middle;
}

.items-list {
  list-style: none;
  padding-left: 0;
  margin: 0;
  font-size: 0.9rem;
  color: #7a5a6d;
  text-align: left;
}

.items-list li {
  margin-bottom: 0.3rem;
}

/* Responsive */
@media (max-width: 600px) {
  .cart-view {
    margin: 1rem;
  }
  .cart-table th,
  .cart-table td {
    padding: 0.5rem;
  }
  .items-list {
    font-size: 0.85rem;
  }
}

.back-btn {
  background: none;
  border: none;
  color: #b46885;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 1rem;
  font-size: 1rem;
}

.back-btn:hover {
  text-decoration: underline;
}

</style>
