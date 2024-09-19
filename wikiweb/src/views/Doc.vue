<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <h3 v-if="level1.items.length === 0">当前书目下没有任何文档。</h3>
      <a-row>
        <a-col :span="6">
          <a-tree
              v-if="level1.items.length > 0"
              :tree-data="level1.items"
              @select="onSelect"
              :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              :defaultExpandAll="true"
              :defaultSelectedKeys="defaultSelectedKeys.items"
              :style="{ fontSize: '16px' }"
          >
          </a-tree>
        </a-col>
        <a-col :span="14">
          <div class="wangeditor" :innerHTML="html.content"></div>
        </a-col>
        <a-col :span="4">
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {createVNode, defineComponent, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import {message, Modal} from 'ant-design-vue';
import {Item, TreeNode, buildTree} from '@/utils/tool'
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

    let docs = reactive({items: []});//存放当前页展示的文档
    const html = reactive({content: ''});

    const handleQueryContent = (id: number) => {
      axios.get("/doc/getContent/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          html.content = data.content;
        } else {
          message.error(data.message);
        }
      });
    };
    const defaultSelectedKeys = reactive<{ items: string[] }>({items:[]});


    const onSelect = (selectedKeys: any, info: any) => {
      console.log('selected', selectedKeys, info);
      handleQueryContent(selectedKeys[0]);
    };

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
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
        const data = response.data;
        if (data.success) {
          docs.items = data.content;
          level1.items = buildTree(docs.items, '0');
          console.log(buildTree(docs.items, '0'));
          if (level1.items.length!=0) {
            defaultSelectedKeys.items = [level1.items[0].id];
            console.log("level1")
            console.log(level1.items[0])
            handleQueryContent(level1.items[0].id as unknown as number);
          }
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
      docs,
      html,
      onSelect,
      defaultSelectedKeys
    }
  }
});
</script>

<style>
/* wangeditor默认样式, 参照: http://www.wangeditor.com/doc/pages/02-%E5%86%85%E5%AE%B9%E5%A4%84%E7%90%86/03-%E8%8E%B7%E5%8F%96html.html */
/* table 样式 */
.wangeditor table {
  border-top: 1px solid #ccc;
  border-left: 1px solid #ccc;
}
.wangeditor table td,
.wangeditor table th {
  border-bottom: 1px solid #ccc;
  border-right: 1px solid #ccc;
  padding: 3px 5px;
}
.wangeditor table th {
  border-bottom: 2px solid #ccc;
  text-align: center;
}

/* blockquote 样式 */
.wangeditor blockquote {
  display: block;
  border-left: 8px solid #d0e5f2;
  padding: 5px 10px;
  margin: 10px 0;
  line-height: 1.4;
  font-size: 100%;
  background-color: #f1f1f1;
}

/* code 样式 */
.wangeditor code {
  display: inline-block;
  *display: inline;
  *zoom: 1;
  background-color: #f1f1f1;
  border-radius: 3px;
  padding: 3px 5px;
  margin: 0 3px;
}
.wangeditor pre code {
  display: block;
}

/* ul ol 样式 */
.wangeditor ul, ol {
  margin: 10px 0 10px 20px;
}

.wangeditor blockquote p {
  font-family:"YouYuan";
  margin: 20px 10px !important;
  font-size: 16px !important;
  font-weight:600;
}
</style>