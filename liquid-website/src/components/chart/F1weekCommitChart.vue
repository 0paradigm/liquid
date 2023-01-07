<template>
  <e-charts :option="option"></e-charts>
</template>

<script>
export default {
  name: "WeekCommitChart",
  props: {
    data1: {
      default: [
        0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0,
      ]
    }
  },
  computed: {
    option() {
      return {
        xAxis: {
          type: 'category',
          data: [
            'Jan. 1 AM',
            'Jan. 1 PM',
            'Jan. 2 AM',
            'Jan. 2 PM',
            'Jan. 3 AM',
            'Jan. 3 PM',
            'Jan. 4 AM',
            'Jan. 4 PM',
            'Jan. 5 AM',
            'Jan. 5 PM',
            'Jan. 6 AM',
            'Jan. 6 PM',
            'Jan. 7 AM',
            'Jan. 7 PM',
          ]
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: this.data1,
            type: 'line',
            lineStyle: {
              color: '#4191de',
              width: 3,
            },
          }
        ]
      }
    }
  }
}
</script>

<style scoped>

</style>