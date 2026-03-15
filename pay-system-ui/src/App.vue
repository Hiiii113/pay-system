<template>
  <RouterView />
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>

<style>
:root {
  --font-sans: 'Plus Jakarta Sans', system-ui, sans-serif;
  --color-bg: #f0f9ff;
  --color-bg-card: #ffffff;
  --color-primary: #0d9488;
  --color-primary-hover: #0f766e;
  --color-primary-light: #ccfbf1;
  --color-accent: #f59e0b;
  --color-accent-hover: #d97706;
  --color-text: #1e293b;
  --color-text-muted: #64748b;
  --color-border: #e2e8f0;
  --color-success: #10b981;
  --color-error: #ef4444;
  --shadow-sm: 0 1px 2px rgba(15, 23, 42, 0.06);
  --shadow-md: 0 4px 12px rgba(15, 23, 42, 0.08);
  --shadow-lg: 0 12px 32px rgba(15, 23, 42, 0.12);
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 24px;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: var(--font-sans);
  font-size: 15px;
  line-height: 1.6;
  color: var(--color-text);
  background: linear-gradient(160deg, #f0fdfa 0%, #f0f9ff 50%, #fefce8 100%);
  min-height: 100vh;
}

#app {
  min-height: 100vh;
}
</style>
