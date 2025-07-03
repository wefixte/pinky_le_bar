<template>
  <div class="carte-view">
    <h1 class="title">Notre Carte</h1>

    <!-- Bouton Commandes que pour Barmaker -->
    <button
      v-if="isBarmaker"
      class="btn-orders"
      @click="goToCommande"
    >
      Voir toutes les commandes
    </button>

    <!-- Bouton Commandes que pour Barmaker -->
    <button
      v-if="isClient"
      class="btn-orders"
      @click="goToMesCommandes"
    >
      Voir toutes mes commandes
    </button>

    <!-- Filtre catégories -->
    <div class="category-filter" v-if="categories.length > 0">
      <button
        v-for="cat in ['Tous', ...categories]"
        :key="cat"
        :class="{ active: selectedCategory === cat }"
        @click="selectedCategory = cat"
      >
        {{ cat }}
      </button>
    </div>

    <div v-if="loading" class="loading">Chargement...</div>
    <div v-else-if="error" class="error">Erreur : {{ error }}</div>
    <div v-else class="grid">
      <CocktailCard
        v-for="cocktail in filteredCocktails"
        :key="cocktail.id"
        :cocktail="cocktail"
      />
    </div>

    <!-- Bouton Ajouter cocktail dispo que pour Barmaker -->
    <button
      v-if="isBarmaker"
      class="btn-add-cocktail"
      @click="goToAddCocktail"
    >
      + Ajouter un cocktail à la carte
    </button>

    <!-- Bouton Accès Panier dispo que pour Client -->
    <button
      v-if="isClient"
      class="btn-go-cart"
      @click="goToCart"
    >
      🛒 Voir mon panier
    </button>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import CocktailCard from '../components/CocktailCard.vue';
import { getAuthHeaders } from '../utils/auth';
import { hasRole } from '../utils/auth';

interface Cocktail {
  id: number;
  name: string;
  image_url: string;
  categoryName: string;
}

const isBarmaker = ref(hasRole('BARMAKER'));
const isClient = ref(hasRole('CLIENT'));
const cocktails = ref<Cocktail[]>([]);
const loading = ref(true);
const error = ref('');

const selectedCategory = ref('Tous');
const categories = ref<string[]>([]);

const router = useRouter();

onMounted(async () => {
  try {
    const res = await axios.get(`/api/cocktails`, {
      headers: getAuthHeaders(),
    });

    if (Array.isArray(res.data)) {
      cocktails.value = res.data.map((c: any) => ({
        id: c.id,
        name: c.name,
        image_url: c.image_url || c.imageUrl,
        categoryName: c.categoryName || 'Autre',
      }));

      // Extraire catégories uniques
      const uniqueCategories = new Set(cocktails.value.map(c => c.categoryName));
      categories.value = Array.from(uniqueCategories).sort();
    } else {
      cocktails.value = [];
      categories.value = [];
    }
  } catch (err) {
    error.value = 'Impossible de récupérer les cocktails';
    console.error(err);
  } finally {
    loading.value = false;
  }
});

const filteredCocktails = computed(() => {
  if (selectedCategory.value === 'Tous') {
    return cocktails.value;
  }
  return cocktails.value.filter(c => c.categoryName === selectedCategory.value);
});

function goToCommande() {
  router.push({ name: 'Orders' });
}

function goToMesCommandes() {
  router.push({ name: 'MesCommandes' });
}

function goToAddCocktail() {
  router.push({ name: 'CocktailCreate' });
}

function goToCart() {
  router.push({ name: 'Cart' });  // <-- il faut que ta route 'Cart' soit déclarée
}

</script>

<style scoped>
.carte-view {
  padding: 2rem;
  max-width: 1200px;
  margin: auto;
  font-family: 'Poppins', sans-serif;
  color: #5a3e4d;
  position: relative; /* Pour positionner le bouton */
}

.title {
  font-weight: 600;
  font-size: 2.25rem;
  margin-bottom: 1.5rem;
  text-align: center;
  color: #7e5a68;
}

/* Filtre catégories */
.category-filter {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 2rem;
}

.category-filter button {
  background-color: #f4d7db;
  border: none;
  border-radius: 20px;
  padding: 0.4rem 1rem;
  font-weight: 600;
  cursor: pointer;
  color: #7e5a68;
  transition: background-color 0.3s ease;
}

.category-filter button.active {
  background-color: #b46885;
  color: white;
}

.category-filter button:hover {
  background-color: #d990a3;
  color: white;
}

.loading {
  color: #a18a99;
  text-align: center;
}

.error {
  color: #b00020;
  text-align: center;
  font-weight: 600;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit,minmax(260px,1fr));
  gap: 1.5rem;
}

/* Bouton Ajouter cocktail fixé en bas à droite */
.btn-add-cocktail {
  position: fixed;
  bottom: 30px;
  right: 30px;
  background-color: #b46885;
  color: white;
  border: none;
  padding: 1rem 1.5rem;
  border-radius: 50px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(180, 104, 133, 0.4);
  transition: background-color 0.3s ease;
  z-index: 1000;
}

.btn-add-cocktail:hover {
  background-color: #7e5a68;
}

.btn-orders {
  position: fixed;
  top: 20px;       /* Espace depuis le haut */
  right: 20px;     /* Espace depuis la droite */
  background-color: #b46885;
  color: white;
  border: none;
  padding: 1rem 1.5rem;
  border-radius: 50px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(180, 104, 133, 0.4);
  transition: background-color 0.3s ease;
  z-index: 1000;
}

.btn-orders:hover {
  background-color: #7e5a68;
}

.btn-go-cart {
  position: fixed;
  bottom: 90px; /* un peu au dessus du bouton ajouter cocktail */
  right: 30px;
  background-color: #5a3e4d;
  color: white;
  border: none;
  padding: 0.9rem 1.3rem;
  border-radius: 50px;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(90, 62, 77, 0.4);
  transition: background-color 0.3s ease;
  z-index: 1000;
}

.btn-go-cart:hover {
  background-color: #7e5a68;
}

@media (max-width: 480px) {
  .btn-orders {
    font-size: 0.9rem;
    padding: 0.7rem 1rem;
    top: 10px;
    right: 10px;
  }
}
</style>
