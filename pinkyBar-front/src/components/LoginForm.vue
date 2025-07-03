<template>
  <form @submit.prevent="onSubmit" class="login-form">
    <h2>Connexion</h2>
    <div class="form-group">
      <label for="email">Email :</label>
      <input id="email" v-model="email" type="email" required autocomplete="username" />
    </div>
    <div class="form-group">
      <label for="password">Mot de passe :</label>
      <input id="password" v-model="password" type="password" required autocomplete="current-password" />
    </div>
    <button type="submit" :disabled="loading">
      {{ loading ? 'Connexion...' : 'Se connecter' }}
    </button>
    <p v-if="error" class="error-message">{{ error }}</p>
  </form>
  <p class="register-link">
    Pas encore inscrit ? <router-link to="/register">S’inscrire</router-link>
  </p>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const router = useRouter()

async function onSubmit() {
  if (!email.value || !password.value) {
    error.value = 'Merci de remplir tous les champs.'
    return
  }

  error.value = ''
  loading.value = true

  try {
    const response = await axios.post(`${import.meta.env.VITE_API_URL}/auth/login`, {
      email: email.value,
      password: password.value,
    })

    const token = response.data.token
    if (!token) {
      throw new Error('Token non reçu')
    }

    // Décoder le token pour récupérer infos utilisateur
    const base64Payload = token.split('.')[1]
    const decodedPayload = JSON.parse(atob(base64Payload))

    const role = decodedPayload.role
    const name = decodedPayload.sub
    const userId = decodedPayload.sub // ou un autre champ, à vérifier selon ton backend

    console.log('Payload du token:', decodedPayload)


    // Stocker les infos dans localStorage
    localStorage.setItem('token', token)
    localStorage.setItem('role', role)
    localStorage.setItem('name', name)
    localStorage.setItem('userId', userId)

    router.push('/carte')
  } catch (e: any) {
    error.value = e.response?.data?.message || e.message || 'Erreur lors de la connexion.'
  } finally {
    loading.value = false
  }
}

</script>
<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap');

.login-form {
  max-width: 320px;
  margin: 3rem auto;
  padding: 2rem 2.5rem;
  background: #fff8fb;
  border-radius: 12px;
  box-shadow: 0 6px 15px rgba(220, 180, 200, 0.25);
  font-family: 'Poppins', sans-serif;
  color: #5a3e4d;
}

.login-form h2 {
  text-align: center;
  margin-bottom: 1.8rem;
  font-weight: 600;
  font-size: 1.8rem;
  letter-spacing: 0.05em;
  color: #7e5a68;
}

.form-group {
  margin-bottom: 1.4rem;
}

.form-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.4rem;
  font-size: 0.9rem;
  letter-spacing: 0.02em;
}

.form-group input {
  width: 100%;
  padding: 0.5rem 0.7rem;
  border: 1.8px solid #d8b5c3;
  border-radius: 8px;
  font-size: 1rem;
  color: #5a3e4d;
  transition: border-color 0.3s ease;
  background-color: #fff;
}

.form-group input:focus {
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

button:disabled {
  background: #e0c9d3;
  cursor: not-allowed;
  color: #9a7a89;
}

button:not(:disabled):hover {
  background: linear-gradient(135deg, #a65f7a, #c48a9d);
}

.error-message {
  color: #b00020;
  margin-top: 1rem;
  font-size: 0.9rem;
  text-align: center;
}

.register-link {
  text-align: center;
  margin-top: 1.6rem;
  font-size: 0.9rem;
  color: #7e5a68;
  font-weight: 500;
}

.register-link a {
  color: #b46885;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #7e5a68;
  text-decoration: underline;
}
</style>