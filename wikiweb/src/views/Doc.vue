<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-row>
        <a-col :span="6">
          <a-tree
              v-if="level1.items.length > 0"
              :tree-data="level1.items"
              @select="onSelect"
              :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              :defaultExpandAll="true"
          >
          </a-tree>
        </a-col>
        <a-col :span="18">
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {createVNode, defineComponent, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import {message, Modal} from 'ant-design-vue';
import { Item, TreeNode, buildTree } from '@/utils/tool'
import {useRoute} from "vue-router";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import E from 'wangeditor'

export default defineComponent({
  name: 'Doc',
  setup() {
    //路由信息
    const route = useRoute();
    console.log("路由：", route);
    console.log("route.path：", route.path);
    console.log("route.query：", route.query);
    console.log("route.param：", route.params);
    console.log("route.fullPath：", route.fullPath);
    console.log("route.name：", route.name);
    console.log("route.meta：", route.meta);

    let queryParams = reactive({name: ''});
    let docs = reactive({items: []});//存放当前页展示的文档

    /**
     * 一级文档树，children属性就是二级文档
     * [{
     *   id: "",
     *   name: "",
     *   children: [{
     *     id: "",
     *     name: "",
     *   }]
     * }]
     */
    const level1 = reactive<{ items: TreeNode[] }>({
      items: []
    }); // 一级文档树，children属性就是二级文档


    /**
     * 数据查询
     **/
    const handleQuery = () => {
      axios.get("/doc/all/"+ route.query.ebookId).then((response) => {
        const data = response.data;
        if (data.success) {
          docs.items = data.content;
          level1.items = buildTree(docs.items,'0');
          console.log(buildTree(docs.items,'0'));
        } else {
          message.error(data.message);
        }
      });
    };




    onMounted(() => {
      handleQuery();
    });

    return {
      level1,
      queryParams,
      docs
    }
  }
});
</script>
