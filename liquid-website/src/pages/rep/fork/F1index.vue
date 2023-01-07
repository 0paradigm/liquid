<template>
  <div class="page" style="padding-top: 30px">
    <div class="left"/>
    <div class="center">
      <div class="title"> Create a new fork</div>
      <div class="text0"> A fork is a copy of a repository. Forking a repository allows you to freely experiment with
        changes without affecting the original project.
      </div>
      <a-divider/>

      <div class="name_setting">
        <div class="left1">
          <div class="title0"> Owner</div>
          <a-input :style="{width:'200px'}" :default-value="me" disabled/>
        </div>

        <div class="right1">
          <div class="title0"> Repository name</div>
          <a-input :style="{width:'200px'}" v-model="repo_name" placeholder="Enter the repo name" allow-clear/>
        </div>
      </div>

      <div>By default, forks are named the same as their upstream repository. You can customize the name to distinguish
        it further.
      </div>
      <div class="title_row">
        <div class="title0"> Description</div>
        <div class="title0" style="color: dimgrey"> (optional)</div>
      </div>
      <a-input :style="{width:'630px'}" v-model="repo_description"
               allow-clear/>
      <a-divider/>

      <div class="radio_row">
        <a-checkbox v-model="masterOnly"/>
        <div class="radio_text">
          <div style="font-family: 'Arial Rounded MT Bold'">Copy the major branch only</div>
          <div style="color: dimgrey; font-size: 10px; padding-top: 2px"> Contribute back to
            {{ `${this.$route.params.user}/${this.$route.params.rep}` }} by adding your own branch.
          </div>
        </div>
      </div>

      <a-divider/>
      <div class="title_row">
        <icon-info-circle-fill style="color: darkslategrey"></icon-info-circle-fill>
        <div style="color: darkslategrey; margin-left: 5px"> {{
            this.$route.params.user == me ? 'You cannot create a fork of your repository' : `You are creating a fork under user ${me}`
          }}.
        </div>
      </div>
      <a-divider/>
      <a-button type="primary" shape="round" class="button" :disabled="this.$route.params.user == me"
                @click="createFork">Create fork
      </a-button>
    </div>
    <div class="right"/>
  </div>
</template>

<script>
import {ref} from "vue";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import {Message} from "@arco-design/web-vue";

export default {
  name: "index",
  data() {
    return {
      repo_name: this.$route.params.rep,
      repo_description: "",
      masterOnly: ref(false),
      me: localStorage.getItem('inUser')
    }
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
    if (!localStorage.getItem('inUser')) {
      this.$router.push('/login')
    }
  },
  methods: {
    createFork() {
      request.get(BACKEND_URL + '/core/api/repo/create',
          {
            params: {
              forkedId: `${this.$route.params.user}/${this.$route.params.rep}`,
              name: this.repo_name,
              private: false,
              description: this.repo_description,
              language: '',
              readme: false,
            }
          })
          .then(resp => {
            console.log(resp.data.code)
            if (resp.data.code != 0) {
              Message.error('A repository with that name already exists')
              return
            }
            this.sleep(800).then(() => {
              this.$router.push({name: 'code', params: {user: localStorage.getItem('inUser'), rep: this.repo_name}})
            })
          })
    },
    sleep(time) {
      return new Promise((resolve) => setTimeout(resolve, time));
    }
  }
}
</script>

<style scoped>
.page {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  margin-top: 8px;
}

.title {
  font-size: 24px;
}

.text0 {
  color: dimgrey;
  margin-top: 8px;
}

.name_setting {
  display: flex;
  flex-direction: row;
  margin-bottom: 10px;
}

.title0 {
  font-family: "Arial Rounded MT Bold";
  margin-bottom: 8px;
  margin-top: 20px;
  margin-right: 4px;
}

.right1 {
  margin-left: 15px;
}

.title_row {
  display: flex;
  flex-direction: row;
}

.radio_row {
  display: flex;
  flex-direction: row;
}

.radio_text {
  margin-left: 8px;
}

.button {
  font-family: "Arial Rounded MT Bold";
}
</style>