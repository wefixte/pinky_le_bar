<!-- Créer ou éditer un cocktail -->
<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold mb-6">Notre Carte</h1>

    <div v-if="loading" class="text-gray-500">Chargement...</div>
    <div v-else-if="error" class="text-red-500">Erreur : {{ error }}</div>
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
      <CocktailCard v-for="cocktail in cocktails" :key="cocktail.id" :cocktail="cocktail" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

interface Cocktail {
  id: number;
  name: string;
  image_url: string;
}

const cocktails = ref<Cocktail[]>([]);
const loading = ref(true);
const error = ref('');

function getAuthHeaders() {
  const token = localStorage.getItem("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
}

onMounted(async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_URL}/api/cocktails`, {
      headers: getAuthHeaders(),
    });
    cocktails.value = res.data;
  } catch (err) {
    error.value = 'Impossible de récupérer les cocktails';
    console.error(err);
  } finally {
    loading.value = false;
  }
});
</script>
