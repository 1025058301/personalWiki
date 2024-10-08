<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登陆名">
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
          :data-source="users.users"
          :pagination="pagination"
          :loading="loading.loading"
          @change="handleTableChange"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="resetPassword(record)">
              重置密码
            </a-button>
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
      title="用户表单"
      v-model:visible="modalVisible.visible"
      :confirm-loading="modalLoading.loading"
      @ok="handleModalOk"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="登陆名">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name"/>
      </a-form-item>
      <a-form-item label="密码" v-show="!user.id">
        <a-input v-model:value="user.password"/>
      </a-form-item>
    </a-form>
  </a-modal>

  <a-modal
      title="重置密码"
      v-model:visible="resetModalVisible.visible"
      :confirm-loading="resetModalLoading.loading"
      @ok="handleResetModalOk"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="新密码">
        <a-input v-model:value="user.password"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'AdminUser',
  setup() {
    const param = reactive({loginName: ''});
    const users = reactive({users: []});
    const pagination = reactive({
      current: 1,
      pageSize: 100,
      total: 0
    });
    const loading = reactive({loading: false});

    const columns = [
      {
        title: '登陆名',
        dataIndex: 'loginName'
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '密码',
        dataIndex: 'password'
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
      loading.loading = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      users.users = [];
      axios.get("/user/list", {
        params: {
          page: params.page,
          size: params.size,
          loginName: param.loginName
        }
      }).then((response) => {
        loading.loading = false;
        const data = response.data;
        if (data.success) {
          users.users = data.content.list;

          // 重置分页按钮
          pagination.current = params.page;
          pagination.total = data.content.total;
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
    const user = reactive({id: '', loginName: '', name: '', password: ''});
    const modalVisible = reactive({visible: false});
    const modalLoading = reactive({loading: false});
    const handleModalOk = () => {
      modalLoading.loading = true;
      user.password = hexMd5(user.password + KEY);
      axios.post("/user/save", user).then((response) => {
        modalLoading.loading = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          modalVisible.visible = false;

          // 重新加载列表
          handleQuery({
            page: pagination.current,
            size: pagination.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 编辑
     */
    const edit = (record: any) => {
      modalVisible.visible = true;
      Object.assign(user, record);
    };

    /**
     * 新增
     */
    const add = () => {
      modalVisible.visible = true;
      Object.assign(user, {id: '', loginName: '', name: '', password: ''});
    };

    const handleDelete = (id: number) => {
      axios.delete("/user/delete/" + id).then((response) => {
        const data = response.data; // data = commonResp
        if (data.success) {
          // 重新加载列表
          handleQuery({
            page: pagination.current,
            size: pagination.pageSize,
          });
        }
      });
    };

    // -------- 重置密码 ---------
    const resetModalVisible = reactive({visible:false});
    const resetModalLoading = reactive({loading:false});
    const handleResetModalOk = () => {
      resetModalLoading.loading = true;

      user.password = hexMd5(user.password + KEY);

      axios.post("/user/resetPassword", user).then((response) => {
        resetModalLoading.loading = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          resetModalVisible.visible = false;

          // 重新加载列表
          handleQuery({
            page: pagination.current,
            size: pagination.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 重置密码
     */
    const resetPassword = (record: any) => {
      resetModalVisible.visible = true;
      Object.assign(user, record);
      user.password = '';
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.pageSize,
      });
    });

    return {
      param,
      users,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,

      edit,
      add,

      user,
      modalVisible,
      modalLoading,
      handleModalOk,
      handleDelete,
      resetModalVisible,
      resetModalLoading,
      handleResetModalOk,
      resetPassword
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
