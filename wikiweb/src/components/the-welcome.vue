<template>
  <div>
    <a-row>
      <a-col :span="24">
        <a-card
            style="background: #f5f5f5; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"
        >
          <h2 style="font-size: 20px; color: #333; text-align: left; font-weight: bold;">欢迎来到我的个人 Wiki 网站</h2>
          <p style="font-size: 16px; color: #666; line-height: 1.6; text-align: justify; margin-top: 10px;">
            Wiki 中的文档无需登录即可浏览和点赞,按钮都可以点击,涉及修改的操作已经加了权限验证。<br>
            文档内容来源于我自己一直以来的一些笔记总结(节选了一部分较为规整的)，后续梳理后或者有新内容会继续增加。
          </p>

          <h3 style="font-size: 18px; color: #333; margin-top: 20px;">聊聊本网站的实现：</h3>
          <p style="font-size: 16px; color: #666; line-height: 1.6; text-align: justify;">
            项目使用的是 Vue3 + Spring Boot 2 的前后端分离架构，使用 MySQL 数据库和 MyBatis 框架，<br>
            Redis做缓存和锁，Kafka做消息队列，前端使用的组件库是 Ant Design Vue，集成的富文本插件是 WangEditor。
          </p>

          <!-- 改进的功能列表 -->
          <h3 style="font-size: 18px; color: #333; margin-top: 20px;">主要功能和实现：</h3>
          <ul style="font-size: 16px; color: #555; line-height: 1.8; margin-top: 10px;">
            <li><span style="color: #1890ff;">1. </span> 使用 Redis + Token + Spring AOP 实现了单点登录和操作的权限验证。</li>
            <li><span style="color: #1890ff;">2. </span> 文档支持无限级树，即每层节点都可以创建下一层节点，依赖于文档字段的设置和递归构造文档树的方法。</li>
            <li><span style="color: #1890ff;">3. </span> 使用 IP 判断进行点赞去重，WebSocket推送点赞通知，Kafka异步点赞和消息推送的操作。</li>
            <li><span style="color: #1890ff;">4. </span> 定时器定时生成电子书快照，依据快照统计网站的浏览和点赞数据，并使用 ECharts 进行展示。</li>
            <li><span style="color: #1890ff;">5. </span> 雪花算法生成分布式唯一主键,SQL使用分页查询，对文档表大字段和小字段进行了垂直拆分，Redis缓存文档内容，减少查询耗时。</li>
            <li><span style="color: #1890ff;">6. </span> 使用布隆过滤器判断用户名的存在，使用redis分布式锁确保缓存未命中时只有单个线程访问数据库，解决缓存穿透与缓存击穿问题。</li>
            <li><span style="color: #1890ff;">7. </span> 使用Kafka和延时队列实现了发生数据更新时缓存的异步延迟双删，保证数据库和缓存的最终一致性。</li>

          </ul>
        </a-card>
      </a-col>
    </a-row>
    <br>
    <a-row>
      <a-col :span="24">
        <a-card>
          <a-row>
            <a-col :span="8">
              <a-statistic title="总阅读量" :value="statistic.viewCount">
                <template #suffix>
                  <UserOutlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="8">
              <a-statistic title="总点赞量" :value="statistic.voteCount">
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="8">
              <a-statistic title="点赞率" :value="statistic.voteCount / statistic.viewCount * 100"
                           :precision="2"
                           suffix="%"
                           :value-style="{ color: '#cf1322' }">
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
    <br>
    <a-row :gutter="16">
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col :span="12">
              <a-statistic title="今日阅读" :value="statistic.todayViewCount" style="margin-right: 50px">
                <template #suffix>
                  <UserOutlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="12">
              <a-statistic title="今日点赞" :value="statistic.todayVoteCount">
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col :span="12">
              <a-statistic
                  title="预计今日阅读"
                  :value="statistic.todayViewIncrease"
                  :value-style="{ color: '#0000ff' }"
              >
                <template #suffix>
                  <UserOutlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="12">
              <a-statistic
                  title="预计今日阅读增长"
                  :value="statistic.todayViewIncreaseRateAbs"
                  :precision="2"
                  suffix="%"
                  class="demo-class"
                  :value-style="statistic.todayViewIncreaseRate < 0 ? { color: '#3f8600' } : { color: '#cf1322' }"
              >
                <template #prefix>
                  <arrow-down-outlined v-if="statistic.todayViewIncreaseRate < 0"/>
                  <arrow-up-outlined v-if="statistic.todayViewIncreaseRate >= 0"/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
    <br>
    <a-row>
      <a-col :span="24">
        <div id="main" style="width: 100%;height:300px;"></div>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
import {defineComponent, ref, onMounted, reactive} from 'vue'
import axios from 'axios';
declare let echarts: any;
export default defineComponent({
  name: 'the-welcome',
  setup() {
    const statistic = reactive({
      viewCount: 0,
      voteCount: 0,
      todayViewCount: 0,
      todayVoteCount: 0,
      todayViewIncrease: 0,
      todayViewIncreaseRate: 0,
      todayViewIncreaseRateAbs: 0
    });
    const getStatistic = () => {
      axios.get('/ebook-snapshot/get-statistic').then((response) => {
        const data = response.data;
        if (data.success) {
          const statisticResp = data.content;
          statistic.viewCount = statisticResp[1].viewCount;
          statistic.voteCount = statisticResp[1].voteCount;
          statistic.todayViewCount = statisticResp[1].viewIncrease;
          statistic.todayVoteCount = statisticResp[1].voteIncrease;

          // 按分钟计算当前时间点，占一天的百分比
          const now = new Date();
          const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);
          // console.log(nowRate)
          statistic.todayViewIncrease = parseInt(String(statisticResp[1].viewIncrease / nowRate));
          const viewIncrease0 = statisticResp[0].viewIncrease == 0? 1 : statisticResp[0].viewIncrease;
          // todayViewIncreaseRate：今日预计增长率，今天的预计增长减去昨天的增长/昨天的增长。
          statistic.todayViewIncreaseRate = (statistic.todayViewIncrease - viewIncrease0) / viewIncrease0 * 100;
          statistic.todayViewIncreaseRateAbs = Math.abs(statistic.todayViewIncreaseRate);
        }
      });
    };
    const init30DayEcharts = (list: any) => {
      // 基于准备好的dom，初始化echarts实例
      const myChart = echarts.init(document.getElementById('main'));

      const xAxis = [];
      const seriesView = [];
      const seriesVote = [];
      for (let i = 0; i < list.length; i++) {
        const record = list[i];
        xAxis.push(record.date);
        seriesView.push(record.viewIncrease);
        seriesVote.push(record.voteIncrease);
      }

      // 指定图表的配置项和数据
      const option = {
        title: {
          text: '30天阅读，点赞趋势图'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['当日阅读量', '当日点赞量']
        },
        grid: {
          left: '1%',
          right: '3%',
          bottom: '3%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xAxis
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '当日阅读量',
            type: 'line',
            // stack: '总量', 不堆叠
            data: seriesView,
            smooth: true
          },
          {
            name: '当日点赞量',
            type: 'line',
            // stack: '总量', 不堆叠
            data: seriesVote,
            smooth: true
          }
        ]
      };

      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    };

    const get30DayStatistic = () => {
      axios.get('/ebook-snapshot/get-30-statistic').then((response) => {
        const data = response.data;
        if (data.success) {
          const statisticList = data.content;

          init30DayEcharts(statisticList)
        }
      });
    };


    onMounted(() => {
      getStatistic();
      get30DayStatistic();
    });

    return {
      statistic
    }
  }
});
</script>
