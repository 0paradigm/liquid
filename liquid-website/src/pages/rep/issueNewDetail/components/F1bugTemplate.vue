<template>
  <a-card style="border-radius:10px;margin-left: 10px">
    <a-badge :count="1" dot :offset="[10, 3]">
      <a-typography-title style="margin-top: 0px" :heading="5">
        Title
      </a-typography-title>
    </a-badge>
    <a-input :style="{borderRadius: '10PX'}" v-model=title allow-clear/>

    <a-typography-title :heading="5">
      Labels
    </a-typography-title>
    <div style="margin-top: 3px;">
      <a-space wrap>
        <a-tag v-for="(tag, index) of tags" :color="tag.color" :closable="index !== 0" style="border-radius: 30px"
               @close="handleRemove(tag.content)">
          {{ tag.content }}
        </a-tag>
        <a-input
            v-if="showInput"
            ref="input"
            :style="{ width: '90px'}"
            size="mini"
            v-model.trim="inputVal"
            @keyup.enter="handleAdd"
            @blur="handleAdd"
        />
        <a-tag
            v-else
            :style="{
        width: '90px',
        backgroundColor: 'var(--color-fill-2)',
        border: '1px dashed var(--color-fill-3)',
        cursor: 'pointer',
      }"
            @click="handleEdit"
        >
          <template #icon>
            <icon-plus/>
          </template>
        </a-tag>
      </a-space>
    </div>
    <a-badge :count="1" dot :offset="[10, 25]" v-if="this.$route.query.type != 'OTHER'">
      <a-typography-title :heading="5">
        Description
      </a-typography-title>
    </a-badge>

    <a-typography-paragraph style="color: var(--color-neutral-6)" v-if="this.$route.query.type != 'OTHER'">
      A clear and concise description of what the bug is.
    </a-typography-paragraph>
    <a-textarea :style="{borderRadius: '10PX'}" placeholder="" v-model="description" allow-clear
                v-if="this.$route.query.type != 'OTHER'"/>
    <a-typography-title :heading="5" v-if="this.$route.query.type != 'OTHER'">
      Steps to reproduce
    </a-typography-title>
    <a-typography-paragraph style="color: var(--color-neutral-6)" v-if="this.$route.query.type != 'OTHER'">
      Steps to reproduce the behavior (if applicable)
    </a-typography-paragraph>
    <a-textarea :style="{height:'100px', borderRadius: '10PX'}" v-model="reproduce" :placeholder=stepPlaceHolder
                allow-clear v-if="this.$route.query.type != 'OTHER'"/>
    <a-badge :count="1" dot :offset="[10, 25]" v-if="this.$route.query.type != 'OTHER'">
      <a-typography-title :heading="5">
        Excepted behavior
      </a-typography-title>
    </a-badge>
    <a-typography-paragraph style="color: var(--color-neutral-6)" v-if="this.$route.query.type != 'OTHER'">
      A clear and concise description of what you expected to happen.
    </a-typography-paragraph>
    <a-textarea :style="{borderRadius: '10PX'}" placeholder="When ..., it should be ..." allow-clear v-model="expected"
                v-if="this.$route.query.type != 'OTHER'"/>
    <a-typography-title :heading="5" v-if="this.$route.query.type != 'OTHER'">
      Screenshots and additional text
    </a-typography-title>
    <a-typography-paragraph style="color: var(--color-neutral-6)" v-if="this.$route.query.type != 'OTHER'">
      If applicable, add screenshots to help explain your problem and any other context about the problem here.
    </a-typography-paragraph>
    <div v-else style="margin-top: 20px"/>
    <mavon-editor style="width: 720px" language="en" placeholder="Please enter something" :subfield=false
                  v-model="others"
                  :autofocus=false></mavon-editor>
    <div style="display: flex;justify-content: space-between;margin-top: 20px">

      <a-badge :count="1" dot :offset="[15, 10]">
        <a-typography-paragraph style="color: var(--color-neutral-6)">
          Fields marked with a red dot are required
        </a-typography-paragraph>
      </a-badge>
      <a-button type="primary" shape="round" @click="submit">Submit new issue</a-button>
    </div>
  </a-card>
</template>

<script>
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import jsCookie from "js-cookie";

export default {
  name: "bugTemplate",
  data() {
    return {
      title: '[' + this.$route.query.type + '] ',
      tags: [],
      description: '',
      reproduce: '',
      expected: '',
      others: '',
      stepPlaceHolder: '1. Go to \'...\' \n2. Click on \'...\'\n3. Scroll down to \'..\'\n4.See error',
      showInput: false,
      inputVal: '',
    }
  },
  mounted() {
    if (this.$route.query.type == 'REQUEST') {
      this.tags.push({content: 'enhancement', color: 'blue'})
    }
    if (this.$route.query.type == 'BUG') {
      this.tags.push({content: 'bug', color: 'red'})
    }
  },
  methods: {
    handleRemove(key) {
      this.tags = this.tags.filter((tag) => tag.content !== key);
    },
    handleAdd() {
      if (this.inputVal) {
        this.tags.push({content: this.inputVal, color: this.random_rgba()});
        this.inputVal = ''
      }
      this.showInput = false
    },
    random_rgba() {
      var o = Math.round, r = Math.random, s = 255;
      return 'rgba(' + o(r() * s) + ',' + o(r() * s) + ',' + o(r() * s) + ',' + r().toFixed(1) + ')';
    },
    handleEdit() {
      this.showInput = true;
      this.$nextTick(() => {
        this.$refs.input.focus();
      });
    },
    submit() {
      let submitStr = ''
      if (!!this.description) {
        submitStr += '## Description\n\n'
        submitStr += this.description + '\n\n'
      }
      if (!!this.reproduce) {
        submitStr += '## Steps to reproduce\n\n'
        submitStr += this.reproduce + '\n\n'
      }
      if (!!this.expected) {
        submitStr += '## Expected behavior\n\n'
        submitStr += this.expected + '\n\n'
      }
      if (!!this.others) {
        submitStr += '## Additional information\n\n'
        submitStr += this.others + '\n\n'
      }

      request.post(BACKEND_URL + `/core/api/issue/new/${this.$route.params.user}/${this.$route.params.rep}`, {
            title: this.title,
            submitStr: submitStr,
            tags: this.tags,
          }, {
            headers: {
              'Authorization': jsCookie.get('Authorization')
            }
          }
      ).then(resp => {
        console.log(resp.data)
        this.$router.push({name: 'issue'})
      })
    },
  }
}
</script>

<style scoped>

</style>