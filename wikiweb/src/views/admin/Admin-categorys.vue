<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="queryParams">
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
          :pagination="false"
          :loading="state.loading"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar"/>
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
    </a-layout-content>
  </a-layout>
  <a-modal
      title="分类表单"
      v-model:visible="modal.visible"
      :confirm-loading="modal.loading"
      @ok="handleModalOk"
  >
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="category.name"/>
      </a-form-item>
      <a-form-item label="父分类">
        <a-select
            v-model:value="category.parent"
            ref="select"
        >
          <a-select-option value="0">
            无
          </a-select-option>
          <a-select-option v-for="c in level1.items" :key="c.id" :value="c.id" :disabled="category.id === c.id">
            {{c.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import { Item, TreeNode, buildTree } from '@/utils/tool'

export default defineComponent({
  name: 'AdminCategory',
  setup() {
    let queryParams = reactive({name: ''});
    let categorys = reactive({items: []});//存放当前页展示的分类
    let category = reactive({id:'',name: '', parent: '', sort: ''});//存放当前编辑的分类的信息
    const state = reactive({loading: false});

    const columns = [
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '父分类',
        key: 'parent',
        dataIndex: 'parent'
      },
      {
        title: '顺序',
        dataIndex: 'sort'
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];
    /**
     * 一级分类树，children属性就是二级分类
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
    }); // 一级分类树，children属性就是二级分类

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      state.loading = true;
      axios.get("/category/all").then((response) => {
        state.loading = false;
        const data = response.data;
        if (data.success) {
          categorys.items = data.content;
          console.log("原始数组：", categorys.items);

          level1.items = buildTree(categorys.items,'0');
          console.log(buildTree(categorys.items,'0'));
          console.log("树形结构：", level1);
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
    const handleModalOk = () => {
      // modal.loading = true;
      axios.post("/category/save", category).then((response) => {
        const data = response.data;
        // modal.loading = false;
        if (data.success) {
          handleQuery();
          modal.visible = false;
        } else {
          message.error(data.message);
        }
      });

    };

    /**
     * 编辑
     */
    const edit = (record: any) => {
      modal.visible = true;
      Object.assign(category, record);//这里将record中的属性复制到category中，而不是直接将category和record进行绑定，所以更改category时，不会影响外部列表中的值
    };
    const add = () => {
      modal.visible = true;
      Object.assign(category, {id:'', name: '', parent: '', sort: ''});
    };

    const handleDelete = (id: number) => {
      axios.delete("/category/delete/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          handleQuery();
        }
      });

    };

    onMounted(() => {
      handleQuery();
    });

    return {
      level1,
      queryParams,
      categorys,
      category,
      columns,
      state,
      modal,
      handleModalOk,
      handleDelete,
      handleQuery,
      edit,
      add
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
