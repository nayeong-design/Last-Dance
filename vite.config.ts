import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// base './' — 정적 호스팅(아티팩트, GitHub Pages) 어디서든 상대 경로로 동작
export default defineConfig({
  base: './',
  plugins: [react()],
  build: { assetsDir: 'bundle' },
});
