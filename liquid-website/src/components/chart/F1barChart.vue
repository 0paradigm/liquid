<template>
  <e-charts :option="option"/>
</template>

<script>
export default {
  name: "barChart",
  props: {
    data: {default: []}
  },
  computed:{
    option(){
      return {
        dataset: {
          source: this.data
        },
        grid: {
          left: '8%',
          right: 0,
          top: '20',
          bottom: '60',
        },
        xAxis: {
          type: 'category',
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
        visualMap: {
          orient: 'horizontal',
          left: 'center',
          min: 0,
          max: 20,
          show: false,
          dimension: 0,
          inRange: {
            color: ['rgba(30, 231, 255, 1)', 'rgba(36, 154, 255, 1)', 'rgb(71,92,175)']
          }
        },
        series: [
          {
            type: 'bar',
            barWidth: 16,
            encode: {
              x: 'AuthorName',
              y: 'commitCnt'
            }
          }
        ],
      }
    }
  },
}
</script>

<style scoped>

</style>