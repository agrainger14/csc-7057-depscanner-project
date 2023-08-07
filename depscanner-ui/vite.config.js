import { defineConfig } from 'vite'
import pluginRewriteAll from 'vite-plugin-rewrite-all'; 
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [
    react(),
    pluginRewriteAll()
  ],
  server: {
    proxy: {
      '/OSV': {
        target: 'https://api.osv.dev',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/OSV/, '/v1/vulns'),
      },
    },
  },
});
