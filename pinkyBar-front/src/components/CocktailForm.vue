<template>
  <div class="cocktail-form">
    <h2>Créer un nouveau cocktail</h2>
    <form @submit.prevent="submitCocktail">
      <div>
        <label for="name">Nom du cocktail :</label>
        <input id="name" v-model="cocktail.name" type="text" required />
      </div>

      <div>
        <label for="description">Description :</label>
        <textarea id="description" v-model="cocktail.description"></textarea>
      </div>

      <div>
        <label for="imageUrl">URL de l'image :</label>
        <input id="imageUrl" v-model="cocktail.imageUrl" type="text" />
      </div>

      <div>
        <label for="category">Catégorie :</label>
        <select id="category" v-model="selectedCategoryId" required>
          <option value="" disabled>Choisir une catégorie</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>
      </div>

      <hr />

      <h3>Ingrédients</h3>
      <div
        v-for="(ing, index) in ingredientsSelected"
        :key="index"
        class="ingredient-row"
      >
        <select v-model="ing.ingredientId" required>
          <option value="" disabled>Choisir un ingrédient</option>
          <option
            v-for="ingredient in ingredients"
            :key="ingredient.id"
            :value="ingredient.id"
          >
            {{ ingredient.name }}
          </option>
        </select>
        <input
          type="number"
          min="0"
          step="0.01"
          v-model.number="ing.quantity"
          placeholder="Quantité en cl"
          required
        />
        <button type="button" @click="removeIngredient(index)">Supprimer</button>
      </div>
      <button type="button" @click="addIngredient">+ Ajouter un ingrédient</button>

      <br />
      <button type="submit" :disabled="loading">
        {{ loading ? "Création..." : "Créer cocktail" }}
      </button>
    </form>

    <hr />

    <div v-if="cocktailCreated">
      <h3>Ajouter les tailles et prix</h3>
      <div v-for="sizeItem in sizes" :key="sizeItem.size" class="size-row">
        <label>{{ sizeItem.size }}</label>
        <input
          type="number"
          min="0"
          step="0.01"
          v-model.number="sizeItem.price"
          placeholder="Prix"
          required
        />
      </div>
      <button type="button" @click="submitSizes" :disabled="sizesLoading">
        {{ sizesLoading ? "Enregistrement..." : "Enregistrer les tailles" }}
      </button>
    </div>

    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="success" class="success">{{ success }}</div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from "vue";
import axios from "axios";

interface Category {
  id: number;
  name: string;
}
interface Ingredient {
  id: number;
  name: string;
}

interface IngredientSelected {
  ingredientId: number | null;
  quantity: number | null;
}

interface SizeItem {
  size: "S" | "M" | "L";
  price: number | null;
}

// État du cocktail
const cocktail = reactive({
  name: "",
  description: "",
  imageUrl: "",
});

const categories = ref<Category[]>([]);
const ingredients = ref<Ingredient[]>([]);

const selectedCategoryId = ref<number | null>(null);

const ingredientsSelected = ref<IngredientSelected[]>([]);

// Pour gérer les tailles après création du cocktail
const sizes = reactive<SizeItem[]>([
  { size: "S", price: null },
  { size: "M", price: null },
  { size: "L", price: null },
]);

const loading = ref(false);
const sizesLoading = ref(false);
const cocktailCreated = ref(false);
const createdCocktailId = ref<number | null>(null);

const error = ref<string | null>(null);
const success = ref<string | null>(null);

function getAuthHeaders() {
  const token = localStorage.getItem("token");
  return token
    ? {
        Authorization: `Bearer ${token}`,
      }
    : {};
}

onMounted(async () => {
  try {
    const [catResp, ingResp] = await Promise.all([
      axios.get(`${import.meta.env.VITE_API_URL}/api/cocktails/categories`, {
        headers: getAuthHeaders(),
      }),
      axios.get(`${import.meta.env.VITE_API_URL}/api/cocktails/ingredients`, {
        headers: getAuthHeaders(),
      }),
    ]);
    categories.value = catResp.data;
    ingredients.value = ingResp.data;
  } catch (e) {
    error.value = "Erreur lors du chargement des catégories ou ingrédients.";
    console.error(e);
  }
});

function addIngredient() {
  ingredientsSelected.value.push({ ingredientId: null, quantity: null });
}

function removeIngredient(index: number) {
  ingredientsSelected.value.splice(index, 1);
}

async function submitCocktail() {
  error.value = null;
  success.value = null;

  if (!cocktail.name || !selectedCategoryId.value) {
    error.value = "Veuillez remplir le nom et la catégorie.";
    return;
  }
  if (
    ingredientsSelected.value.some(
      (ing) => !ing.ingredientId || !ing.quantity || ing.quantity <= 0
    )
  ) {
    error.value = "Veuillez renseigner tous les ingrédients avec une quantité > 0.";
    return;
  }

  loading.value = true;

  try {
    const cocktailData = {
      name: cocktail.name,
      description: cocktail.description,
      imageUrl: cocktail.imageUrl,
    };
    const resp = await axios.post(`${import.meta.env.VITE_API_URL}/api/cocktails`, cocktailData, {
      params: { categoryId: selectedCategoryId.value },
      headers: getAuthHeaders(),
    });
    createdCocktailId.value = resp.data.id;

    for (const ing of ingredientsSelected.value) {
      await axios.post(
        `${import.meta.env.VITE_API_URL}/api/cocktails/${createdCocktailId.value}/ingredients`,
        null,
        {
          params: {
            ingredientId: ing.ingredientId,
            quantity: ing.quantity?.toString() || "0",
          },
          headers: getAuthHeaders(),
        }
      );
    }

    cocktailCreated.value = true;
    success.value = "Cocktail créé avec succès. Maintenant, ajoutez les tailles et prix.";
  } catch (e: any) {
    error.value = e.response?.data?.message || "Erreur lors de la création du cocktail.";
  } finally {
    loading.value = false;
  }
}

async function submitSizes() {
  if (!createdCocktailId.value) {
    error.value = "Cocktail non créé.";
    return;
  }
  sizesLoading.value = true;
  error.value = null;
  success.value = null;

  try {
    for (const sizeItem of sizes) {
      if (sizeItem.price === null || sizeItem.price <= 0) {
        throw new Error(`Prix invalide pour la taille ${sizeItem.size}`);
      }
      await axios.post(
        `${import.meta.env.VITE_API_URL}/api/cocktails/${createdCocktailId.value}/sizes`,
        null,
        {
          params: {
            size: sizeItem.size,
            price: sizeItem.price,
          },
          headers: getAuthHeaders(),
        }
      );
    }

    success.value = "Tailles enregistrées avec succès !";
  } catch (e: any) {
    error.value =
      e.response?.data?.message || e.message || "Erreur lors de l'enregistrement des tailles.";
  } finally {
    sizesLoading.value = false;
  }
}
</script>

<style scoped>
.cocktail-form {
  max-width: 600px;
  margin: 2rem auto;
  font-family: 'Poppins', sans-serif;
  color: #5a3e4d;
  background: #fff8fb;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(180, 104, 133, 0.15);
}

.cocktail-form h2,
.cocktail-form h3 {
  font-weight: 700;
  color: #7e5a68;
  margin-bottom: 1rem;
}

form > div,
.ingredient-row,
.size-row {
  margin-bottom: 1rem;
}

label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.3rem;
  color: #7e5a68;
}

input[type="text"],
input[type="number"],
textarea,
select {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1.5px solid #d7b8c4;
  border-radius: 8px;
  font-size: 1rem;
  color: #5a3e4d;
  font-family: 'Poppins', sans-serif;
  transition: border-color 0.3s ease;
}

input[type="text"]:focus,
input[type="number"]:focus,
textarea:focus,
select:focus {
  border-color: #b46885;
  outline: none;
}

textarea {
  min-height: 80px;
  resize: vertical;
}

.ingredient-row,
.size-row {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.ingredient-row select,
.size-row input,
.ingredient-row input {
  flex: 1;
}

/* Style bouton communs */
button {
  background-color: #b46885;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 0.75rem; /* Espace au-dessus des boutons */
}

button:hover:not(:disabled) {
  background-color: #7e5a68;
}

button[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Pour les boutons + Ajouter un ingrédient et Créer cocktail */
.cocktail-form > form > button[type="button"] {
  margin-top: 0.75rem; /* espace au-dessus */
  margin-bottom: 1.5rem; /* espace entre ce bouton et celui de création */
}

/* Espace avant le bouton Créer cocktail */
.cocktail-form > form > button[type="submit"] {
  margin-top: 1rem;
}

/* Style spécifique au hr */
hr {
  margin: 2rem 0;
  border-color: #d7b8c4;
}

/* Messages d'erreur et succès */
.error {
  margin-top: 1rem;
  color: #b00020;
  font-weight: 600;
  text-align: center;
}

.success {
  margin-top: 1rem;
  color: #2a7a2a;
  font-weight: 600;
  text-align: center;
}
</style>