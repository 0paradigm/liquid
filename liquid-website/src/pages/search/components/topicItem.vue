<template>
  <a-space align="start" style="margin-top: 10px; margin-bottom: 10px">
    <a-avatar :size="25">Arco</a-avatar>
    <a-space direction="vertical">
      <div class="title_container">
        <a-link style="font-size: 15px">{{ title }}</a-link>
        <a-button style="margin-left: 600px;" shape="round">
          <icon-star />
          Star
        </a-button>
      </div>
      <a-typography-text style="font-size: 10px">{{ descrip }}</a-typography-text>
      <a-space>
        <div style="font-size: 10px; color: var(--color-neutral-6)">{{ repo_num }}k+ repositories</div>
        <div style="font-size: 10px; color: var(--color-neutral-6)">Related:</div>
        <div>
          <a-link v-for="item in related_list" :href="item.content" :hoverable="false"
                  style="margin-right: 5px; font-size: 2px">{{ item.value }}
          </a-link>
        </div>
      </a-space>
    </a-space>
  </a-space>
</template>

<script>
export default {
  name: "topicItem",
  data() {
    return {
      title: 'Java',
      descrip: 'Java is an object-oriented programming language used mainly for web, desktop, embedded devices and mobile applications.',
      repo_num: 30,
      language: 'HTML',
      timeStamp: '6 days ago',
      related_list: [
        {value: 'Java-11', content: 'link1'},
        {value: 'Java-8', content: 'link2'},
        {value: 'Java-se', content: 'link3'},
        {value: 'aaa', content: 'link4'},
        {value: 'bbbb', content: 'link5'},
        {value: 'ccc', content: 'link6'}
      ]

    }
  }
}
</script>

<style scoped>

</style>