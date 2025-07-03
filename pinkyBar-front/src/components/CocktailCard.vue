<!--	Carte individuelle cocktail (image + nom + clic vers détails) -->
<template>
  <div
    class="cocktail-card"
    @click="$router.push(`/cocktails/${cocktail.id}`)"
  >
    <img :src="cocktail.image_url" :alt="cocktail.name" class="cocktail-image" />
    <h3 class="cocktail-name">{{ cocktail.name }}</h3>
  </div>
</template>

<script lang="ts" setup>
import { defineProps } from 'vue';

interface Cocktail {
  id: number;
  name: string;
  image_url: string;
}

const props = defineProps<{ cocktail: Cocktail }>();
</script>

<style scoped>
.cocktail-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(180, 104, 133, 0.15);
  cursor: pointer;
  transition: box-shadow 0.3s ease;
  background-color: #fff8fb;
}

.cocktail-card:hover {
  box-shadow: 0 8px 24px rgba(180, 104, 133, 0.4);
}

.cocktail-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
  display: block;
}

.cocktail-name {
  margin: 0.8rem;
  font-weight: 600;
  font-size: 1.25rem;
  color: #7e5a68;
  text-align: center;
}
</style>