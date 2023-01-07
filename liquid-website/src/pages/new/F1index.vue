<template>
  <div class="page">
    <div class="left"/>
    <div class="center">
      <a-list :bordered="false">
        <template #header>
          <div class="Title" style="font-size: 40px">Create a new repository</div>
          <div class="Discription" style="font-size:15px">
            A repository contains all project files, including the revision history.
          </div>
        </template>

        <a-list-item class="first-list">
          <div class="Info Title" style="font-size: 22px">Repository Info</div>
          <div class="first-input" style="align-content: flex-start">
            <div style="margin-top: 10px" class="repo-name-input">
              <div class="Repo-name-discrip" style="font-size: 15px">
                Repository name
              </div>
              <a-input v-model="name" :style="{width:'320px', borderRadius:'3px', marginTop:'5px'}"
                       allow-clear/>
            </div>
          </div>
          <div style="display: flex">
            Great repository names are short and memorable. Need inspiration? How about&nbsp;
            <b style="color: #09a872">
              scaling-octo-engine
            </b>?
          </div>
          <div style="margin-top: 10px" class="second-input">
            <div class="repo-discription-input">
              <div class="Repo-discription-discrip" style="font-size: 15px">
                Description
              </div>
              <a-input v-model="description" :style="{width:'700px', borderRadius:'3px'}"
                       placeholder="Optional" allow-clear/>
            </div>
          </div>
        </a-list-item>

        <a-list-item>
          <div class="third-select">
            <div class="Visibility Title" style="font-size: 22px">Visibility</div>
            <t-radio-group variant="default-filled" v-model="isPrivate" class="radio1">
              <t-radio-button :value=false>
                <icon-unlock/>
                Public
              </t-radio-button>
              <t-radio-button :value=true>
                <icon-lock/>
                Private
              </t-radio-button>
            </t-radio-group>
          </div>
        </a-list-item>

        <a-list-item>
          <div class="init" style="font-size: 22px">
            Initialize this repository with
          </div>

          <t-checkbox class="readme checkbox" v-model="withReadme">Add a README file</t-checkbox>

          <div class="gitignore" style="font-size: 15px">
            Add .gitignore Template: &nbsp;&nbsp;

            <a-select v-model="language" :style="{width:'320px'}" placeholder="Choose a language" allow-search>
              <a-option value="C">C</a-option>
              <a-option value="C++">C++</a-option>
              <a-option value="Python">Python</a-option>
              <a-option value="Java">Java</a-option>
            </a-select>
          </div>

        </a-list-item>

        <a-list-item>
          <t-button block @click="createRepo">Create repository</t-button>
        </a-list-item>
      </a-list>

    </div>

    <div class="right"/>
  </div>


</template>

<script>
import request from "@/utils/request";
import {mapGetters} from 'vuex'
import {BACKEND_URL} from "@/utils/config";
import {Message} from "@arco-design/web-vue";

export default {
  name: "index",
  computed: {
    ...mapGetters('user', ['getUser'])
  },
  created() {
    document.title = 'Create Repository - liquid'
  },
  data() {
    return {
      name: '',
      description: '',
      isPrivate: false,
      language: '',
      withReadme: true,
    }
  },
  methods: {
    createRepo() {
      if (!/^[A-Za-z0-9_-]+$/.test(this.name)) {
        Message.error('Invalid repository name')
        return
      }
      request.get(BACKEND_URL + '/core/api/repo/create',
          {
            params: {
              forkedId: null,
              name: this.name,
              private: this.isPrivate,
              description: this.description,
              language: this.language,
              readme: this.withReadme,
            }
          })
          .then(resp => {
            console.log(resp.data.code)
            if (resp.data.code != 0) {
              Message.error('A repository with that name already exists')
              return
            }
            this.sleep(800).then(() => {
              Message.success('success')
              this.$router.push({name: 'code', params: {user: localStorage.getItem('inUser'), rep: this.name}})
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
}

.first-input {
  /*display: grid;*/
  /*grid-template-columns: 1fr 1fr;*/
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.first-list {
  margin-top: 5px;
}

.second-input {
  margin-top: 5px;
}

.third-select {
  display: grid;
  grid-template-rows: 1fr 1fr;
}

.radio1 {
  margin-top: 5px;
}

.init {
  margin-top: 5px;
  margin-bottom: 10px;
}

.readme {
  margin-bottom: 5px;
}

.gitignore {
  margin-top: 10px;
  margin-bottom: 10px;
}

.license {
  margin-top: 10px;
  margin-bottom: 10px;
}


.info {
  /*font-size: 25px;*/
}

</style>