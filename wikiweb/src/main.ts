import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import 'ant-design-vue/dist/antd.css';
import Antd from 'ant-design-vue';
import * as Icons from '@ant-design/icons-vue';
import axios from "axios";
import {Tool} from "@/utils/tool";
import {message} from 'ant-design-vue';
axios.defaults.baseURL=process.env.VUE_APP_SERVER
axios.interceptors.request.use(function (config) {
    console.log('请求参数：', config);
    const token = store.state.user.token;
    if (Tool.isNotEmpty(token)) {
        config.headers.token = token;
        console.log("请求headers增加token:", token);
    }
    return config;
}, error => {
    return Promise.reject(error);
});
axios.interceptors.response.use(function (response) {
    console.log('返回结果：', response);
    return response;
}, error => {
    if (error.response) {
        const status = error.response.status;
        // 处理不同的 HTTP 状态码
        switch (status) {
            case 401:
                message.error('没有执行该操作的权限，需要登录');  // 输出 401 错误消息
                // 可执行其他操作，如跳转到登录页面：
                // window.location.href = '/login';
                break;
            case 500:
                message.error('服务器内部错误，请稍后再试');  // 输出 500 错误消息
                break;
            default:
                message.error(`请求失败，状态码：${status}`);  // 输出默认错误消息
        }}
    console.log('返回错误：', error);
    return Promise.reject(error);
});


const app=createApp(App)
app.use(store).use(router).use(Antd).mount('#app')
// 全局使用图标
const icons: any = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}



console.log("当前环境",process.env.NODE_ENV)
console.log("服务端地址",process.env.VUE_APP_SERVER)