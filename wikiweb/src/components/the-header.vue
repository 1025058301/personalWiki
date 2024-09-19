<template>
  <a-layout-header class="header">
    <div class="logo"/>
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="home">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="about">
        <router-link to="/about">关于</router-link>
      </a-menu-item>
      <a-menu-item key="user">
        <router-link to="/admin/user">用户管理</router-link>
      </a-menu-item>
      <a-menu-item key="admin">
        <router-link to="/admin/ebook">wiki书目管理</router-link>
      </a-menu-item>
      <a-menu-item key="category">
        <router-link to="/admin/category">wiki分类管理</router-link>
      </a-menu-item>
      <a class="login-menu" v-show="user.id">
        <span>您好：{{user.name}}</span>
      </a>
      <a class="login-menu" v-show="!user.id" @click="showLoginModal">
        <span>登录</span>
      </a>
    </a-menu>
  </a-layout-header>

  <a-modal
      title="登录"
      v-model:visible="loginModalVisible.visible"
      :confirm-loading="loginModalLoading.loading"
      @ok="login"
  >
    <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="登录名">
        <a-input v-model:value="loginUser.loginName" />
      </a-form-item>
      <a-form-item label="密码">
        <a-input v-model:value="loginUser.password" type="password" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts">
import {defineComponent, reactive} from 'vue';
import {ref} from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'the-header',
  setup () {
    const user = reactive({id:'',loginName:'',name:'',token:''});
    const loginUser = reactive({
      loginName: "test",
      password: "test"
    });
    const loginModalVisible = reactive({visible:false});
    const loginModalLoading = reactive({loading:false});
    const showLoginModal = () => {
      loginModalVisible.visible = true;
    };

    // 登录
    const login = () => {
      console.log("开始登录");
      loginModalLoading.loading = true;
      loginUser.password = hexMd5(loginUser.password + KEY);
      axios.post('/user/login', loginUser).then((response) => {
        loginModalLoading.loading = false;
        const data = response.data;
        Object.assign(user, data.content);
        console.log("user")
        console.log(user)
        if (data.success) {
          loginModalVisible.visible = false;
          message.success("登录成功！");
        } else {
          message.error(data.message);
        }
      });
    };

    return {
      loginModalVisible,
      loginModalLoading,
      showLoginModal,
      loginUser,
      login,
      user
    }
  }
});
</script>


<style>
.login-menu {
  float: right;
  color: white;
  margin-right: 200px;
}
</style>