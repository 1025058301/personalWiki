<template>
  <a-layout style="display: flex">
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
      >
        <a-menu-item key="welcome">
          <MailOutlined/>
          <span>欢迎</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1.items" :key="item.id" @titleClick=handleClick({})>
          <template v-slot:title>
            <span><user-outlined/>{{ item.name }}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined/>
            <span>{{ child.name }}</span>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px',flex:1 }"
    >
      <div class="welcome" v-show="isShowWelcome.show">
        <h1>欢迎使用知识库</h1>
      </div>
      <a-list item-layout="vertical" v-show="!isShowWelcome.show" size="large" :grid="{ gutter: 20, column: 4 }"
              :data-source="ebooks.books">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
              <span v-for="{ type, text } in actions" :key="type">
                <component v-bind:is="type" style="margin-right: 8px"/>
                {{ text }}
              </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <a :href="item.href">{{ item.name }}</a>
              </template>
              <template #avatar>
                <a-avatar :src="item.cover" shape="square" :size="50"/>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>


<script lang="ts">
import {UserOutlined, LaptopOutlined, NotificationOutlined} from '@ant-design/icons-vue';
import {defineComponent, onMounted, reactive, ref, toRef} from 'vue';
import axios from 'axios';
import {buildTree, TreeNode} from "@/utils/tool";
import {message} from "ant-design-vue";

export default defineComponent({
  components: {
    UserOutlined,
    LaptopOutlined,
    NotificationOutlined,
  },
  setup() {
    const level1 = reactive<{ items: TreeNode[] }>({
      items: []
    }); // 一级分类树，children属性就是二级分类
    let categorys: any[] = [];
    /**
     * 数据查询
     **/
    const handleCategory = () => {
      state.loading = true;
      axios.get("/category/all").then((response) => {
        state.loading = false;
        const data = response.data;
        if (data.success) {
          categorys = data.content
          console.log("原始数组：", categorys);
          level1.items = buildTree(categorys, '0');
          console.log("树形结构：", level1);
        } else {
          message.error(data.message);
        }
      });
    };
    const ebooks = reactive({books: []});
    const state = reactive({loading: true});
    onMounted(() => {
      handleCategory();
      console.log("on Mounted");
      axios.get("/ebook/list", {
        params: {page: 1, size: 100}
      }).then((response) => {
        console.log(response);
        const data = response.data;
        ebooks.books = data.content.list;
      })
    });
    state.loading = false;

    //菜单点击逻辑
    const isShowWelcome = reactive({show: true});
    const handleClick = (value: any) => {
      if (value.key == 'welcome') {
        isShowWelcome.show = true;
      } else {
        isShowWelcome.show = false;
      }
    }

    return {
      state,
      ebooks,
      level1,
      isShowWelcome,
      handleCategory,
      handleClick,
      pagination: {
        onChange: (page: any) => {
          console.log(page);
        },
        pageSize: 3,
      },
      actions: [
        {type: 'StarOutlined', text: '156'},
        {type: 'LikeOutlined', text: '156'},
        {type: 'MessageOutlined', text: '2'},
      ],
    };
  },
});
</script>
