import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    // port: 3000,
    proxy: {
      '/api': {
        // [C practice] temporarily pointed at the MCP-enabled backend on 8081 so the browser
        // and the MCP tools share one backend. Revert to 8080 when done.
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
    },
  },
})
