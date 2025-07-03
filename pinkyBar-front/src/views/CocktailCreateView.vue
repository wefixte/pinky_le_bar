<template>
  <div class="cocktail-create-view">
    <button class="btn-return" @click="goBackToCarte">← Revenir à la carte</button>
    <CocktailForm />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import CocktailForm from '../components/CocktailForm.vue';

const router = useRouter();

function goBackToCarte() {
  router.push({ name: 'Carte' });
}
</script>

<style scoped>
.cocktail-create-view {
  padding: 20px;
  position: relative;
}

/* Style bouton en haut à gauche */
.btn-return {
  position: absolute;
  top: 20px;
  left: 20px;
  background-color: #b46885;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  z-index: 10;
}

.btn-return:hover {
  background-color: #7e5a68;
}
</style>
