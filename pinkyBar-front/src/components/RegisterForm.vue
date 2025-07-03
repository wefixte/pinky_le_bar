<template>
  <div class="register-container">
    <h2>Inscription</h2>
    <form @submit.prevent="register" class="register-form">
      <div class="form-group">
        <label for="name">Nom :</label>
        <input id="name" v-model="name" type="text" placeholder="Nom" required autocomplete="name" />
      </div>

      <div class="form-group">
        <label for="email">Email :</label>
        <input id="email" v-model="email" type="email" placeholder="Email" required autocomplete="email" />
      </div>

      <div class="form-group">
        <label for="password">Mot de passe :</label>
        <input id="password" v-model="password" type="password" placeholder="Mot de passe" required autocomplete="new-password" />
      </div>

      <div class="form-group">
        <label for="role">Rôle :</label>
        <select id="role" v-model="role" required>
          <option disabled value="">Sélectionner un rôle</option>
          <option value="CLIENT">Client</option>
          <option value="BARMAKER">Barmaker</option>
        </select>
      </div>

      <button type="submit">S’inscrire</button>
    </form>

    <p class="login-link">
      Déjà inscrit ? <router-link to="/login">Se connecter</router-link>
    </p>

    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const name = ref('')
const email = ref('')
const password = ref('')
const role = ref('')

const successMessage = ref('')
const errorMessage = ref('')

const register = async () => {
  successMessage.value = ''
  errorMessage.value = ''

  try {
    const res = await axios.post(`${import.meta.env.VITE_API_URL}/auth/register`, {
      name: name.value,
      email: email.value,
      password: password.value,
      role: role.value
    })
    // Si on arrive ici, l'inscription a réussi
    successMessage.value = 'Inscription réussie ! Vous pouvez maintenant vous connecter.'
    // Optionnel : reset des champs
    name.value = ''
    email.value = ''
    password.value = ''
    role.value = ''
  } catch (error) {
    errorMessage.value = error.response?.data?.message || error.message || 'Erreur lors de l’inscription.'
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap');

.register-container {
  max-width: 320px;
  margin: 3rem auto;
  padding: 2rem 2.5rem;
  background: #fff8fb;
  border-radius: 12px;
  box-shadow: 0 6px 15px rgba(220, 180, 200, 0.25);
  font-family: 'Poppins', sans-serif;
  color: #5a3e4d;
}

.register-container h2 {
  text-align: center;
  margin-bottom: 1.8rem;
  font-weight: 600;
  font-size: 1.8rem;
  letter-spacing: 0.05em;
  color: #7e5a68;
}

.register-form .form-group {
  margin-bottom: 1.4rem;
}

.register-form label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.4rem;
  font-size: 0.9rem;
  letter-spacing: 0.02em;
}

.register-form input,
.register-form select {
  width: 100%;
  padding: 0.5rem 0.7rem;
  border: 1.8px solid #d8b5c3;
  border-radius: 8px;
  font-size: 1rem;
  color: #5a3e4d;
  background-color: #fff;
  transition: border-color 0.3s ease;
}

.register-form input:focus,
.register-form select:focus {
  outline: none;
  border-color: #b46885;
  box-shadow: 0 0 8px rgba(180, 104, 133, 0.4);
}

button {
  width: 100%;
  background: linear-gradient(135deg, #b46885, #d1a3b0);
  color: white;
  font-weight: 600;
  font-size: 1.1rem;
  padding: 0.65rem 0;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  transition: background 0.4s ease;
  letter-spacing: 0.04em;
}

button:hover {
  background: linear-gradient(135deg, #a65f7a, #c48a9d);
}

.login-link {
  text-align: center;
  margin-top: 1.6rem;
  font-size: 0.9rem;
  color: #7e5a68;
  font-weight: 500;
}

.login-link a {
  color: #b46885;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #7e5a68;
  text-decoration: underline;
}

.response-box {
  margin-top: 1.8rem;
  background-color: #f8e8ef;
  border: 1px solid #d8b5c3;
  border-radius: 10px;
  padding: 1rem;
  font-size: 0.9rem;
  color: #7e5a68;
  white-space: pre-wrap;
  max-height: 150px;
  overflow-y: auto;
}

.success-message {
  margin-top: 1.8rem;
  background-color: #d7f4e3;
  border: 1px solid #7bcf91;
  border-radius: 10px;
  padding: 1rem;
  font-size: 1rem;
  color: #2f6b37;
  text-align: center;
}

.error-message {
  margin-top: 1.8rem;
  background-color: #f8e8ef;
  border: 1px solid #d8b5c3;
  border-radius: 10px;
  padding: 1rem;
  font-size: 0.9rem;
  color: #b00020;
  text-align: center;
}
</style>
