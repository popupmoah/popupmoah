import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { Quasar, Notify, Dialog, Loading } from 'quasar'

// Import Quasar css
import '@quasar/extras/material-icons/material-icons.css'
import 'quasar/dist/quasar.css'

import App from './App.vue'
import router from './router'

// Import global styles
import './style.css'

const app = createApp(App)

// Quasar 플러그인 설정
app.use(Quasar, {
  plugins: {
    Notify,
    Dialog,
    Loading,
  },
  config: {
    brand: {
      primary: '#3b82f6',
      secondary: '#64748b',
      accent: '#f59e0b',
      dark: '#1e293b',
      darkPage: '#0f172a',
      positive: '#10b981',
      negative: '#ef4444',
      info: '#3b82f6',
      warning: '#f59e0b',
    },
    notify: {
      position: 'top-right',
      timeout: 3000,
    },
  },
})

app.use(createPinia())
app.use(router)

app.mount('#app')
