import { createApp } from 'vue'
import App from './App.vue'
import { initRouter } from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import './assets/main.css'

initRouter()
createApp(App).use(ElementPlus).mount('#app')
