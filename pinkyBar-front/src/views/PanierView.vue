<template>
  <div class="cart-view">
    <button @click="$router.back()" class="back-btn">← Retour</button>
    <h1>Mon panier</h1>

    <div v-if="confirmationMessage" class="confirmation-message">
      ✅ {{ confirmationMessage }}
    </div>

    <div v-else-if="cartItems.length === 0" class="empty">
      Votre panier est vide.
    </div>

    <div v-else>
      <table class="cart-table">
        <thead>
          <tr>
            <th>Cocktail</th>
            <th>Taille</th>
            <th>Quantité</th>
            <th>Prix unitaire</th>
            <th>Total</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in cartItems" :key="item.cocktailId + '-' + item.size">
            <td>{{ item.cocktailName }}</td>
            <td>{{ item.size }}</td>
            <td>
              <button @click="decrement(index)">-</button>
              {{ item.quantity }}
              <button @click="increment(index)">+</button>
            </td>
            <td>{{ item.price }} €</td>
            <td>{{ (item.price * item.quantity).toFixed(2) }} €</td>
            <td>
              <button @click="removeItem(index)" class="btn-remove">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="cart-total">
        <strong>Total :</strong> {{ totalPrice.toFixed(2) }} €
      </div>

      <button class="btn-order" @click="placeOrder" :disabled="isLoading">
        {{ isLoading ? 'Validation...' : 'Passer la commande' }}
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue';
import { useCartStore } from '../stores/cart';
import { getAuthHeaders } from '../utils/auth';
import axios from 'axios';

const cartStore = useCartStore();
const cartItems = computed(() => cartStore.items);
const isLoading = ref(false);
const confirmationMessage = ref('');

// Fonction pour décoder le payload JWT et récupérer sub
function parseJwt(token: string) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
}

const totalPrice = computed(() =>
  cartStore.items.reduce((sum, item) => sum + item.price * item.quantity, 0)
);

function increment(index: number) {
  cartStore.incrementQuantity(index);
}

function decrement(index: number) {
  cartStore.decrementQuantity(index);
}

function removeItem(index: number) {
  cartStore.removeItem(index);
}

async function placeOrder() {
  if (cartItems.value.length === 0) {
    alert('Votre panier est vide.');
    return;
  }

  const token = localStorage.getItem('token');
  if (!token) {
    alert('Utilisateur non connecté');
    return;
  }
  const payload = parseJwt(token);
  const userEmail = payload?.sub;
  if (!userEmail) {
    alert('Impossible de récupérer l\'email utilisateur');
    return;
  }

  isLoading.value = true;

  console.log(cartItems.value);

  const requestBody = {
  items: cartStore.items.map(item => ({
    cocktailSizeId: item.cocktailSizeId, // obligatoire côté backend
    quantity: item.quantity,
    })),
  };

  console.log('Payload envoyé au backend:', JSON.stringify(requestBody, null, 2));

  try {
    const response = await axios.post(
    `/api/orders`,
    requestBody,
    {
      headers: {
        ...getAuthHeaders(),
        'Content-Type': 'application/json',
      },
    }
);

    const createdOrder = response.data;
    confirmationMessage.value = `Commande créée avec succès ! Numéro : ${createdOrder.id}`;

    cartStore.clear();

    setTimeout(() => {
      confirmationMessage.value = '';
    }, 5000);
  } catch (error) {
    let message = 'Erreur inconnue';
    if (typeof error === 'object' && error !== null) {
      const err = error as { response?: { data?: string }, message?: string };
      message = 'Erreur lors de la validation du panier : ' + (err.response?.data || err.message);
    }
    alert(message);
  } finally {
    isLoading.value = false;
  }
}
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
}
.btn-remove {
  background-color: #b00020;
  color: white;
  border: none;
  padding: 0.3rem 0.6rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.btn-remove:hover {
  background-color: #7a0015;
}
.cart-total {
  font-size: 1.3rem;
  text-align: right;
  margin-bottom: 2rem;
}
.btn-order {
  display: block;
  margin: 0 auto;
  padding: 0.75rem 2rem;
  background-color: #b46885;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.btn-order:hover:not(:disabled) {
  background-color: #a05d78;
}
.btn-order:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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