<template>
  <a-space align="start" style="margin-top: 10px; margin-bottom: 10px">
    <icon-bookmark size="20"/>
    <a-space direction="vertical">
      <a-link @click="this.$router.push({name:'code', params:{user:user, rep:repo}})"
              :hoverable="false"
              style="font-size: 15px">
        {{ user }}/{{ repo }}
      </a-link>
      <a-typography-text style="font-size: 10px">{{ description }}</a-typography-text>
      <a-space>
        <div>
          <icon-star-fill style="color:rgb(var(--orange-5));"/>
          {{ star }}
        </div>
        <div style="font-size: 10px; color: var(--color-neutral-6)">{{ language }}</div>
        <!--        <div style="font-size: 10px; color: var(&#45;&#45;color-neutral-6)">Updated {{timeStamp}}</div>-->
      </a-space>
    </a-space>
  </a-space>
</template>

<script>
export default {
  name: "repItem",
  props: {
    repo: {default: 'DAFTAR-API-LOKAL-INDONESIA'},
    user: {default: 'farizdotid'},
    description: {default: 'Berisi API tentang Data Indonesia ataupun API yang dibuat oleh developer Indonesia <3'},
    star: {default: 30},
    language: {default: 'java'},
  },
  data() {
    return {}
  }
}
</script>

<style scoped>

</style>