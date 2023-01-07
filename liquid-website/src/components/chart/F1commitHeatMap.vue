<template>
  <div>
    <calendar-heatmap end-date="2023-01-08" :values="timeValue" tooltip-unit="commits"/>
  </div>
</template>
<script>
import { CalendarHeatmap } from 'vue3-calendar-heatmap'
export default {
  props: {
    timeValue: {default: []}
  },
  components: {
    CalendarHeatmap,
  },
};
</script>
