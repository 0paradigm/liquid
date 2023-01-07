<template>
  <a-link :hoverable="false">
    <div class="head">
      <icon-tag />
      <div class="title">{{title}}</div>
      <a-tag class="tag" size="mini" color="blue">Latest</a-tag>
    </div>
      <div class="date">{{date}}</div>
  </a-link>
</template>

<script>
export default {
  name: "release_element",
  data(){
    return{
      title:'1.2.0-rc1',
      date:'on Jan 1, 2023'
    }
  }
}
</script>

<style scoped>
.head{
  display: flex;
  margin-top: 6px;
  flex-direction: row;
  font-size:15px;
  font-family: "Arial Rounded MT Bold";
}
.title{
  margin-left: 4px;
}
.date{
  margin-left: 20px;
  font-size:8px;
  font-family: "Avenir Next";
}
.tag{
  margin-left: 7px;
}
</style>