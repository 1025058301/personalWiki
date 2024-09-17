<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="queryParams">
          <a-form-item>
            <a-input v-model:value="queryParams.name" placeholder="名称">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
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
          :data-source="categorys.items"
          :pagination="pagination"
          :loading="state.loading"
          @change="handleTableChange"
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
        <a-input v-model:value="category.parent"/>
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

export default defineComponent({
  name: 'AdminCategory',
  setup() {
    let queryParams = reactive({name: ''});
    let categorys = reactive({items: []});//存放当前页展示的分类
    let category = reactive({ name: '', parent: '', sort: ''});//存放当前编辑的分类的信息
    const state = reactive({loading: false});
    const pagination = reactive({
      current: 1,
      pageSize: 4,
      total: 0
    });

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
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      state.loading = true;
      axios.get("/category/list", {params: {page: params.page, size: params.size, name: queryParams.name}}).then((response) => {
        state.loading = false;
        const data = response.data;
        if (data.success) {
          categorys.items = data.content.list;
          // 重置分页按钮
          pagination.current = params.page;
          pagination.total = data.content.totalsPages;
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    // -------- 表单 ---------
    const modal = reactive({loading: false, visible: false});
    const handleModalOk = () => {
      modal.loading = true;
      axios.post("/category/save", category).then((response) => {
        const data = response.data;
        modal.loading = false;
        if (data.success) {
          handleQuery({
            page: pagination.current,
            size: pagination.pageSize,
          });
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
      Object.assign(category, {name: '', parent: '', category2Id: '', description: ''});
    };

    const handleDelete = (id: number) => {
      axios.delete("/category/delete/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          if (categorys.items.length == 1 && pagination.current > 1) {
            handleQuery({
              page: pagination.current - 1,
              size: pagination.pageSize,
            });
          } else {
            handleQuery({
              page: pagination.current,
              size: pagination.pageSize,
            });
          }
        }
      });

    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.pageSize
      });
    });

    return {
      queryParams,
      categorys,
      category,
      pagination,
      columns,
      state,
      modal,
      handleTableChange,
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
