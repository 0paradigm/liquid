<template>
  <e-charts class="chart" :option="option"></e-charts>
</template>

<script>
import {graphic} from 'echarts';

export default {
  name: "lineChart",
  mounted() {
    this.data = [
      {x:'Mon', y: 0},
      {x:'Tue', y: 10},
      {x:'Wed', y: 2},
      {x:'Thu', y: 3},
      {x:'Fri', y: 3}
    ]
  },
  data() {
    return {
      data : []
    }
  },
  computed:{
    option(){
      return  {
        grid: {
          left: '40',
          right: '20',
          top: '20',
          bottom: '100',
        },
        legend: {
          bottom: 0,
          icon: 'circle',
          textStyle: {
            color: '#4E5969',
          },
        },
        xAxis: {
          type: 'category',
          data: this.data.map(item=>item.x),
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: '#A9AEB8',
            },
          },
          axisTick: {
            show: true,
            alignWithLabel: true,
            lineStyle: {
              color: '#86909C',
            },
          },
          axisLabel: {
            color: '#86909C',
          },
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            color: '#86909C',
            formatter: '{value}',
          },
          splitLine: {
            lineStyle: {
              color: '#E5E6EB',
            },
          },
        },
        tooltip: {
          show: true,
          trigger: 'axis',
          className: 'echarts-tooltip-diy',
        },
        series:[
          {
            data: this.data.map(item=>item.y),
            type: 'line',
            smooth: true,
            // symbol: 'circle',
            symbolSize: 12,
            emphasis: {
              focus: 'series',
              itemStyle: {
                borderWidth: 2,
              },
            },
            lineStyle: {
              width: 3,
              color: new graphic.LinearGradient(0, 0, 1, 0, [
                {
                  offset: 0,
                  color: 'rgba(30, 231, 255, 1)',
                },
                {
                  offset: 0.5,
                  color: 'rgba(36, 154, 255, 1)',
                },
                {
                  offset: 1,
                  color: 'rgba(111, 66, 251, 1)',
                },
              ]),
            },
            showSymbol: false,
            areaStyle: {
              opacity: 0.8,
              color: new graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(17, 126, 255, 0.16)',
                },
                {
                  offset: 1,
                  color: 'rgba(17, 128, 255, 0)',
                },
              ]),
            },
          }],
        dataZoom: [
          {
            bottom: 40,
            type: 'slider',
            left: 40,
            right: 10,
            height: 14,
            borderColor: 'transparent',
            handleIcon:
                'image://http://p3-armor.byteimg.com/tos-cn-i-49unhts6dw/1ee5a8c6142b2bcf47d2a9f084096447.svg~tplv-49unhts6dw-image.image',
            handleSize: '20',
            handleStyle: {
              shadowColor: 'rgba(0, 0, 0, 0.2)',
              shadowBlur: 4,
            },
            brushSelect: false,
            backgroundColor: '#F2F3F5',
          },
          {
            type: 'inside',
            start: 0,
            end: 100,
            zoomOnMouseWheel: false,
          },
        ],
      }
    }
  },

}
</script>

<style scoped>

</style>