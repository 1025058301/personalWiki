<template>
  <a-layout-header class="header">
    <div class="logo">知识库</div>
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="home">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="user" v-if="user.id">
        <router-link to="/admin/user">用户管理</router-link>
      </a-menu-item>
      <a-menu-item key="admin">
        <router-link to="/admin/ebook">wiki书目管理</router-link>
      </a-menu-item>
      <a-menu-item key="category">
        <router-link to="/admin/category">wiki分类管理</router-link>
      </a-menu-item>
      <a-popconfirm
          title="确认退出登录?"
          ok-text="是"
          cancel-text="否"
          @confirm="logout()"
      >
        <a class="login-menu" v-show="user.id">
          <span>退出登录</span>
        </a>
      </a-popconfirm>
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
import {computed, defineComponent, reactive} from 'vue';
import {ref} from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import store from "@/store";

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'the-header',
  setup () {
    const user = computed(() => store.state.user);
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
        if (data.success) {
          loginModalVisible.visible = false;
          message.success("登录成功！");
          store.commit("setUser", data.content);
          console.log(data.content)
        } else {
          message.error(data.message);
        }
      });
    };
    // 退出登录
    const logout = () => {
      console.log("退出登录开始");
      axios.get('/user/logout/' + user.value.token).then((response) => {
        const data = response.data;
        if (data.success) {
          message.success("退出登录成功！");
          store.commit("setUser", {});
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
      user,
      logout
    }
  }
});
</script>


<style>
.login-menu {
  float: right;
  color: white;
  margin-right: 100px;
  padding-left: 10px;
}
.logo {
  width: 120px;
  height: 31px;
  background: rgba(10, 9, 9, 0.2);
  margin: 16px 28px 16px 40px;
  float: left;
  color: white;
  font-size: 18px;
}
</style>