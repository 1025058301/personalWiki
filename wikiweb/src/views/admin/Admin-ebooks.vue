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
        <a-input v-model:value="ebook.description" type="textarea"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';

export default defineComponent({
  name: 'AdminEbook',
  setup() {
    let queryParams = reactive({name: ''});
    let ebooks = reactive({books: []});//存放当前页展示的电子书
    let ebook = reactive({cover: '', name: '', category1Id: '', category2Id: '', description: ''});//存放当前编辑的电子书的信息
    const state = reactive({loading: false});
    const pagination = reactive({
      current: 1,
      pageSize: 4,
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
      axios.get("/ebook/list", {params: {page: params.page, size: params.size, name: queryParams.name}}).then((response) => {
        state.loading = false;
        const data = response.data;
        if (data.success) {
          ebooks.books = data.content.list;
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
      axios.post("/ebook/save", ebook).then((response) => {
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
      Object.assign(ebook, record);//这里将record中的属性复制到ebook中，而不是直接将ebook和record进行绑定，所以更改ebook时，不会影响外部列表中的值
    };
    const add = () => {
      modal.visible = true;
      Object.assign(ebook, {cover: '', name: '', category1Id: '', category2Id: '', description: ''});
    };

    const handleDelete = (id: number) => {
      axios.delete("/ebook/delete/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          if (ebooks.books.length == 1 && pagination.current > 1) {
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
      ebooks,
      ebook,
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
