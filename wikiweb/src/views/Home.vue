<template>
  <a-layout style="display: flex">
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
      >
        <a-menu-item key="welcome " @Click="() => handleClick({ key: 'welcome' })">
          <span>欢迎</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1.items" :key="item.id"
                    @titleClick="() => handleClick({ category1Id: item.id })" )>
          <template v-slot:title>
            <span><user-outlined/>{{ item.name }}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id"
                       @Click="() => handleClick({ category2Id: child.id })">
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
        <TheWelcome></TheWelcome>
      </div>
      <a-list item-layout="vertical" v-show="!isShowWelcome.show" size="large" :grid="{ gutter: 20, column: 3 }"
              :data-source="ebooks.books">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
              <span>
                <component v-bind:is="'FileOutlined'" style="margin-right: 8px" />
                {{ item.docCount }}
              </span>
              <span>
                <component v-bind:is="'UserOutlined'" style="margin-right: 8px" />
                {{ item.viewCount }}
              </span>
              <span>
                <component v-bind:is="'LikeOutlined'" style="margin-right: 8px" />
                {{ item.voteCount }}
              </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{ item.name }}
                </router-link>
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
import TheWelcome from "@/components/the-welcome.vue";

export default defineComponent({
  components: {
    UserOutlined,
    LaptopOutlined,
    NotificationOutlined,
    TheWelcome,
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
    });
    const handleQueryEbooks = (value: any) => {
      state.loading = false;
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 100,
          category1Id: value.category1Id,
          category2Id: value.category2Id
        }
      }).then((response) => {
        console.log(response);
        const data = response.data;
        ebooks.books = data.content.list;
      })
    }
    //菜单点击逻辑
    const isShowWelcome = reactive({show: true});
    const handleClick = (value: any) => {
      console.log(value);
      if (value.key == 'welcome') {
        isShowWelcome.show = true;
      } else {
        if ('category1Id' in value) {
          handleQueryEbooks({category1Id: value.category1Id})
        } else if ('category2Id' in value) {
          handleQueryEbooks({category2Id: value.category2Id})
        }
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
    };
  },
});
</script>
