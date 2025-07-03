<template>
  <div class="details-view">
    <button @click="$router.back()" class="back-btn">← Retour</button>

    <div v-if="loading" class="loading">Chargement...</div>
    <div v-else-if="error" class="error">Erreur : {{ error }}</div>
    <div v-else-if="cocktail" class="content">
      <h1 class="name">{{ cocktail.name }}</h1>
      <p class="description">{{ cocktail.description }}</p>

      <img
        :src="cocktail.imageUrl"
        :alt="cocktail.name"
        class="image"
      />

      <section class="section">
        <h2>Catégorie</h2>
        <p>{{ cocktail.categoryName }}</p>
      </section>

      <section class="section">
        <h2>Ingrédients</h2>
        <ul>
          <li v-for="ingredient in cocktail.ingredients" :key="ingredient.ingredientId">
            {{ ingredient.ingredientName }} : {{ ingredient.quantity }} L
          </li>
        </ul>
      </section>

      <!-- Section tailles + quantité + ajouter au panier visible uniquement pour USER -->
      <section v-if="isClient" class="section order-section">
        <h2>Choisissez votre taille et quantité</h2>
        <div class="sizes-buttons">
          <button
            v-for="(size, index) in cocktail.sizes"
            :key="index"
            :class="{ selected: selectedSize === size.size }"
            @click="selectSize(size.size)"
            type="button"
          >
            {{ size.size.toUpperCase() }} - {{ size.price }} €
          </button>
        </div>

        <div class="quantity-control" role="group" aria-label="Contrôle de la quantité">
          <button @click="decrementQuantity" type="button" aria-label="Réduire la quantité">−</button>
          <span class="quantity-display" aria-live="polite">{{ quantity }}</span>
          <button @click="incrementQuantity" type="button" aria-label="Augmenter la quantité">+</button>
        </div>

        <button
          class="btn-add-to-cart"
          :disabled="!selectedSize"
          @click="addToCart"
          type="button"
        >
          Ajouter au panier
        </button>
        <div v-if="showConfirmation" class="confirmation-message">
            Cocktail ajouté au panier !
            <button @click="goToCart" class="btn-see-cart">Voir le panier</button>
        </div>
      </section>

      <button v-if="isBarmaker" @click="deleteCocktail" class="btn-delete" :disabled="deleting">
        {{ deleting ? 'Suppression...' : 'Supprimer ce cocktail' }}
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import { getAuthHeaders, hasRole } from '../utils/auth';
import { useCartStore } from '../stores/cart';

const isBarmaker = ref(hasRole('BARMAKER'));
const isClient = ref(hasRole('CLIENT')); // <-- ici on vérifie USER

interface Ingredient {
  ingredientId: number;
  ingredientName: string;
  quantity: number;
}

interface Size {
  id: number;
  size: string;
  price: number;
}

interface Cocktail {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
  categoryName: string;
  ingredients: Ingredient[];
  sizes: Size[];
}

const route = useRoute();
const router = useRouter();
const id = route.params.id as string;

const cocktail = ref<Cocktail | null>(null);
const loading = ref(true);
const error = ref('');
const deleting = ref(false);

const selectedSize = ref<string | null>(null);
const quantity = ref(1);
const showConfirmation = ref(false);

const cartStore = useCartStore();

onMounted(async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_URL}/api/cocktails/${id}`, {
      headers: getAuthHeaders(),
    });
    cocktail.value = res.data;
    console.log('Cocktail data:', cocktail.value);
  } catch (err) {
    error.value = 'Impossible de récupérer les détails du cocktail';
    console.error(err);
  } finally {
    loading.value = false;
  }
});

function selectSize(size: string) {
  selectedSize.value = size;
}

function incrementQuantity() {
  quantity.value++;
}

function decrementQuantity() {
  if (quantity.value > 1) quantity.value--;
}

function addToCart() {
  if (!cocktail.value || !selectedSize.value) return;


  console.log('Ta taille sélectionnée:', selectedSize.value);
  console.log('Ta liste de tailles:', cocktail.value.sizes);

  // Trouve le prix correspondant à la taille sélectionnée
  const sizeObj = cocktail.value.sizes.find(s => s.size === selectedSize.value);
 
  console.log('sizeObj trouvé:', sizeObj);
  
  if (!sizeObj) {
    alert('Prix non trouvé pour cette taille');
    return;
  }

  if (!sizeObj.id) {
    alert('ID de la taille non défini');
    return;
  }

  const item = {
    cocktailSizeId: sizeObj.id,
    cocktailId: cocktail.value.id,
    cocktailName: cocktail.value.name,
    size: selectedSize.value,
    quantity: quantity.value,
    price: sizeObj.price,
  };

  console.log('Ajout au panier:', item);

  cartStore.addItem(item);
  showConfirmation.value = true;

  // Reset sélection / quantité
  quantity.value = 1;
  selectedSize.value = null;
}

async function deleteCocktail() {
  if (!confirm('Êtes-vous sûr de vouloir supprimer ce cocktail ?')) {
    return;
  }
  deleting.value = true;
  try {
    await axios.delete(`${import.meta.env.VITE_API_URL}/api/cocktails/${id}`, {
      headers: getAuthHeaders(),
    });
    alert('Cocktail supprimé avec succès.');
    router.push({ name: 'Carte' });
  } catch (err) {
    alert('Erreur lors de la suppression du cocktail.');
    console.error(err);
  } finally {
    deleting.value = false;
  }
}

function goToCart() {
  router.push({ name: 'Cart' });
}
</script>

<style scoped>
.btn-see-cart {
  background-color: white;
  color: #b46885;
  border: 2px solid white;
  padding: 0.45rem 1.2rem;
  border-radius: 24px;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(180, 104, 133, 0.3);
  transition:
    background-color 0.3s ease,
    color 0.3s ease,
    box-shadow 0.3s ease,
    border-color 0.3s ease;
  white-space: nowrap;
}

.btn-see-cart:hover {
  background-color: #f7e7ea;
  color: #933e60;
  border-color: #933e60;
  box-shadow: 0 6px 12px rgba(147, 62, 96, 0.5);
}

.confirmation-message {
  margin-top: 1rem;
  padding: 1rem 1.5rem;
  background: linear-gradient(135deg, #b46885, #933e60);
  color: white;
  border-radius: 12px;
  box-shadow: 0 8px 15px rgba(180, 104, 133, 0.4);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  font-weight: 600;
  font-size: 1rem;
  animation: fadeInUp 0.4s ease forwards;
}

.details-view {
  max-width: 720px;
  margin: 2rem auto;
  padding: 1rem 2rem;
  font-family: 'Poppins', sans-serif;
  color: #5a3e4d;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
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

.loading,
.error {
  text-align: center;
  font-weight: 600;
  margin: 2rem 0;
}

.loading {
  color: #a18a99;
}

.error {
  color: #b00020;
}

.name {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.description {
  margin-bottom: 1.5rem;
  color: #7e5a68;
  font-size: 1.1rem;
  line-height: 1.4;
}

.image {
  width: 100%;
  max-width: 420px;
  border-radius: 12px;
  margin-bottom: 2rem;
  object-fit: cover;
  display: block;
  margin-left: auto;
  margin-right: auto;
}

.section {
  margin-bottom: 2rem;
}

.section h2 {
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #7e5a68;
  font-size: 1.3rem;
  border-bottom: 1px solid #e3d6db;
  padding-bottom: 0.4rem;
}

.section ul {
  list-style: disc inside;
  color: #5a3e4d;
  padding-left: 0;
  font-size: 1rem;
  line-height: 1.3;
}

/* Tailles + quantité + bouton ajouter au panier */
.order-section {
  border: 1px solid #d8b9c3;
  padding: 1.5rem 2rem;
  border-radius: 12px;
  background-color: #fff5f8;
  max-width: 480px;
  margin: 0 auto 2rem auto;
}

.sizes-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.sizes-buttons button {
  padding: 0.6rem 1.3rem;
  border: 2px solid #b46885;
  background: white;
  cursor: pointer;
  border-radius: 8px;
  font-weight: 600;
  color: #b46885;
  font-size: 1rem;
  transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
  min-width: 70px;
  text-align: center;
}

.sizes-buttons button.selected,
.sizes-buttons button:hover {
  background-color: #b46885;
  color: white;
  border-color: #933e60;
}

.quantity-control {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 1.5rem;
}

.quantity-control button {
  width: 36px;
  height: 36px;
  font-weight: 700;
  border-radius: 8px;
  border: 2px solid #b46885;
  background: white;
  cursor: pointer;
  color: #b46885;
  font-size: 1.3rem;
  line-height: 1;
  transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
}

.quantity-control button:hover {
  background-color: #b46885;
  color: white;
  border-color: #933e60;
}

.quantity-display {
  font-weight: 700;
  font-size: 1.4rem;
  min-width: 30px;
  text-align: center;
  color: #5a3e4d;
  user-select: none;
}

.btn-add-to-cart {
  display: block;
  margin: 0 auto;
  padding: 0.8rem 2rem;
  background-color: #b46885;
  color: white;
  font-weight: 700;
  font-size: 1.1rem;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 100%;
  max-width: 320px;
}

.btn-add-to-cart:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-add-to-cart:hover:not(:disabled) {
  background-color: #933e60;
}

/* Bouton supprimer */
.btn-delete {
  background-color: #d9534f;
  color: white;
  border: none;
  font-weight: 700;
  padding: 0.7rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  display: block;
  margin: 0 auto;
  max-width: 280px;
  transition: background-color 0.3s ease;
}

.btn-delete:hover:not(:disabled) {
  background-color: #b52b27;
}

/* Responsive */
@media (max-width: 480px) {
  .sizes-buttons {
    gap: 0.6rem;
  }
  .sizes-buttons button {
    padding: 0.5rem 0.9rem;
    font-size: 0.9rem;
    min-width: 50px;
  }
  .quantity-control button {
    width: 30px;
    height: 30px;
    font-size: 1.1rem;
  }
  .btn-add-to-cart {
    max-width: 100%;
    font-size: 1rem;
  }
}
</style>
