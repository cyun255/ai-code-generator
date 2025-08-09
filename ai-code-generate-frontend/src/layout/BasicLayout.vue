<template>
  <div class="basic-layout">
    <GlobalHeader />
    <main class="main">
      <slot />
    </main>
    <GlobalFooter />
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '../components/GlobalHeader.vue'
import GlobalFooter from '../components/GlobalFooter.vue'
</script>

<style lang="scss" scoped>
.basic-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;

  .main {
    flex: 1;
    padding-top: 24px;
    background: #f7f8fa;
  }
}

@media (max-width: 600px) {
  .basic-layout {
    .main {
      padding-left: 0;
      padding-right: 0;
    }
  }
}
</style>
