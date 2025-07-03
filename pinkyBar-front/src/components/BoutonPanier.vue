<template>
  <button @click="goToCart" class="cart-button">
    🛒 Panier ({{ totalQuantity }})
  </button>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useCartStore } from '../stores/cart';
import { useRouter } from 'vue-router';

const cartStore = useCartStore();
const router = useRouter();

const totalQuantity = computed(() => cartStore.totalQuantity);

function goToCart() {
  router.push({ name: 'Cart' });
}
</script>


<style scoped>
.cart-button {
  background-color: #b46885;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 0.4rem 0.8rem;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.cart-button:hover {
  background-color: #924e64;
}
</style>
