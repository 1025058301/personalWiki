<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row>
        <a-col :span="8">
          <p>
            <a-form layout="inline" :model="queryParams">
              <a-form-item>
                <a-button type="primary" @click="handleQuery()">
                  查询
                </a-button>
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="add()">
                  新增
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1.items"
              :loading="state.loading"
              :pagination="false"
          >
            <template #name="{ text, record }">
              {{record.sort}} {{text}}
            </template>
            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)">
                  编辑
                </a-button>
                <a-popconfirm
                    title="删除后不可恢复，确认删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
                >
                  <a-button type="danger">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="queryParams">
              <a-form-item>
                <a-button type="primary" @click="handleModalSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称" />
            </a-form-item>
            <a-form-item>
              <a-tree-select
                  v-model:value="doc.parent"
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  :tree-data="treeSelectData.items"
                  placeholder="请选择父文档"
                  tree-default-expand-all
                  :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序" />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent()">
                <EyeOutlined /> 内容预览
              </a-button>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible.visible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml.content"></div>
      </a-drawer>

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
  name: 'AdminDoc',
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
    let doc = reactive({ebookId:'',id:'',name: '', parent: '', sort: '',viewCount: null, voteCount: null,content:''});//存放当前编辑的文档的信息
    const state = reactive({loading: false});

    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
        slots: { customRender: 'name' }
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ];
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
    const treeSelectData = reactive<{ items: TreeNode[] }>({
      items: []
    });

    /**
     * 将某节点及其子孙节点全部置为disabled
     */
    const setDisable = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          console.log("disabled", node);
          // 将目标节点设置为disabled
          node.disabled = true;

          // 遍历所有子节点，将所有子节点全部都加上disabled
          if ('children' in node) {
            const children=node.children
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if ('children' in node) {
            const children=node.children;
            setDisable(children, id);
          }
        }
      }
    };
    //找到所有要删除的文档
    const deleteIds: Array<string> = [];
    const deleteNames: Array<string> = [];

    const getDeleteIds = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          // 遍历所有子节点，将所有子节点全部都加上disabled
          deleteIds.push(id);
          deleteNames.push(node.name)
          if ('children' in node) {
            const children=node.children
            for (let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if ('children' in node) {
            const children=node.children;
            getDeleteIds(children, id);
          }
        }
      }
    };


    /**
     * 数据查询
     **/
    const handleQuery = () => {
      state.loading = true;
      axios.get("/doc/all/"+ route.query.ebookId).then((response) => {
        state.loading = false;
        const data = response.data;
        if (data.success) {
          docs.items = data.content;
          level1.items = buildTree(docs.items,'0');
          console.log(buildTree(docs.items,'0'));
          treeSelectData.items = JSON.parse(JSON.stringify(level1.items));
          treeSelectData.items.unshift({id: '0', name: '无',parent:'0',sort:0})
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 富文本查询
     **/

    const handleQueryContent = () => {
      axios.get("/doc/getContent/"+doc.id).then((response) => {
        const data = response.data;
        if (data.success) {
          editor.txt.html(data.content)
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 表格点击页码时触发
     */

        // -------- 表单 ---------
    const modal = reactive({loading: false, visible: false});
    const handleModalSave = () => {
      modal.loading = true;
      doc.content=editor.txt.html() as string
      if(doc.ebookId==''){
        doc.ebookId=route.query.ebookId as string
      }
      axios.post("/doc/save", doc).then((response) => {
        const data = response.data;
        modal.loading = false;
        if (data.success) {
          message.success("保存成功！");
          handleQuery();
        } else {
          message.error(data.message);
        }
      });

    };
    const editor = new E('#content');
    editor.config.zIndex = 0;

    /**
     * 编辑
     */
    const edit = (record: any) => {
      modal.visible = true;
      editor.txt.html("");
      Object.assign(doc, record);//这里将record中的属性复制到doc中，而不是直接将doc和record进行绑定，所以更改doc时，不会影响外部列表中的值
      handleQueryContent();

      // treeSelectData.items = level1.items.map(item => ({ ...item }));
      treeSelectData.items = JSON.parse(JSON.stringify(level1.items));
      // console.log("打印树型结构")
      // console.log(treeSelectData.items)
      setDisable(treeSelectData.items, record.id);
      treeSelectData.items.unshift({id: '0', name: '无',parent:'0',sort:0})
      // console.log("打印修改后的树型结构")
      // console.log(treeSelectData)
    };
    const add = () => {
      modal.visible = true;
      editor.txt.html("");
      Object.assign(doc, {ebookId:'',id:'',name: '', parent: '', sort: '',viewCount: null, voteCount: null,content:''});
      console.log("add打印doc")
      console.log(doc)
      doc.ebookId=route.query.ebookId as string
      treeSelectData.items = JSON.parse(JSON.stringify(level1.items));
      treeSelectData.items.unshift({id: '0', name: '无',parent:'0',sort:0})
    };

    const handleDelete = (id: number) => {
      deleteIds.length=0;
      deleteNames.length=0;
      getDeleteIds(level1.items,id)
      Modal.confirm({
        title: '重要提醒',
        icon: createVNode(ExclamationCircleOutlined),
        content: '将删除：【' + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
        onOk() {
          // console.log(ids)
          axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
            const data = response.data; // data = commonResp
            if (data.success) {
              // 重新加载列表
              handleQuery();
            }
          });
        },
      });
    };

    const drawerVisible = reactive({visible:false});
    const previewHtml = reactive({content:''});
    const handlePreviewContent = () => {
      const html = editor.txt.html();
      previewHtml.content = html as string;
      drawerVisible.visible = true;
    };
    const onDrawerClose = () => {
      drawerVisible.visible = false;
    };

    onMounted(() => {
      handleQuery();
      editor.create();
    });

    return {
      level1,
      queryParams,
      docs,
      doc,
      columns,
      state,
      modal,
      treeSelectData,
      handleModalSave,
      handleDelete,
      handleQuery,
      edit,
      add,
      drawerVisible,
      previewHtml,
      handlePreviewContent,
      onDrawerClose,
    }
  }
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
