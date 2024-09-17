<template>
  <a-layout-header class="header">
    <div class="logo"/>
    <a-menu
        theme="dark"
        mode="horizontal"
        v-model:selectedKeys="selectedKeys1"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="home">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="about">
        <router-link to="/about">关于</router-link>
      </a-menu-item>
      <a-menu-item key="admin">
        <router-link to="/admin/ebook">wiki书目管理</router-link>
      </a-menu-item>
      <a-menu-item key="category">
        <router-link to="/admin/category">wiki分类管理</router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>
<script lang="ts">
import {defineComponent} from 'vue';
import {ref} from 'vue';

export default defineComponent({
  name: 'the-header',
  setup() {
    return {
      selectedKeys1: ref<string[]>(['home'])
    };
  },
},);
</script>


<style scoped>

</style>