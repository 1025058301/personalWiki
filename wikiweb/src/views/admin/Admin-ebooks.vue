<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-button type="primary" @click="add()" size="large">
          新增
        </a-button>
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks.books"
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
            <a-button type="danger">
              删除
            </a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
      title="电子书表单"
      v-model:visible="modal.visible"
      :confirm-loading="modal.loading"
      @ok="handleModalOk"
  >
    <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover"/>
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name"/>
      </a-form-item>
      <a-form-item label="分类一">
        <a-input v-model:value="ebook.category1Id"/>
      </a-form-item>
      <a-form-item label="分类二">
        <a-input v-model:value="ebook.category2Id"/>
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.desc" type="textarea"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, reactive, ref} from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'AdminEbook',
  setup() {
    let ebooks = reactive({books: []});//存放当前页展示的电子书
    let ebook = reactive({cover: '', name: '', category1Id: '', category2Id: '', desc: ''});//存放当前编辑的电子书的信息
    const state = reactive({loading: false});
    const pagination = reactive({
      current: 1,
      pageSize: 3,
      total: 0
    });

    const columns = [
      {
        title: '封面',
        dataIndex: 'cover',
        slots: {customRender: 'cover'}
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '分类一',
        key: 'category1Id',
        dataIndex: 'category1Id'
      },
      {
        title: '分类二',
        dataIndex: 'category2Id'
      },
      {
        title: '文档数',
        dataIndex: 'docCount'
      },
      {
        title: '阅读数',
        dataIndex: 'viewCount'
      },
      {
        title: '点赞数',
        dataIndex: 'voteCount'
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
      axios.get("/ebook/list", {params: {page: params.page, size: params.size}}).then((response) => {
        state.loading = false;
        const data = response.data;
        ebooks.books = data.content.list;

        // 重置分页按钮
        pagination.current = params.page;
        pagination.total = data.content.totalsPages;
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
      axios.post("/ebook/save", ebook).then((response) => {
        const data = response.data;
        if(data.success){
          handleQuery({
            page: pagination.current,
            size: pagination.pageSize,
          });
          modal.loading=false;
          modal.visible=false;
        }
      });

    };

    /**
     * 编辑
     */
    const edit = (record: any) => {
      modal.visible = true;
      Object.assign(ebook,record)
    };
    const add = () => {
      modal.visible = true;
      Object.assign(ebook, {cover: '', name: '', category1Id: '', category2Id: '', desc: ''});
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.pageSize
      });
    });

    return {
      ebooks,
      ebook,
      pagination,
      columns,
      state,
      modal,
      handleTableChange,
      handleModalOk,
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
