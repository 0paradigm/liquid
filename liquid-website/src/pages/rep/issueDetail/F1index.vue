<template>
  <div style="display: flex; justify-content: center">
    <div>
      <div class="header-container">
        <div style="display: flex; flex-direction: row; justify-content: space-between; margin-bottom: -15px">
          <a-typography-title :heading="2" style=''>
            {{ title }}
          </a-typography-title>
        </div>

        <div>
          <a-space>
            <a-tag size=large color="rgb(var(--green-3))" style="border-radius: 20px;" v-if="isOpening">
              <template #icon>
                <icon-minus-circle style="color:rgb(var(--green-7))"/>
              </template>
              Open
            </a-tag>
            <a-tag size=large color="rgb(var(--arcoblue-3))" style="border-radius: 20px;" v-else>
              <template #icon>
                <icon-check-circle style="color:rgb(var(--arcoblue-4))"/>
              </template>
              Closed
            </a-tag>
            <a-typography-paragraph style="color:var(--color-neutral-6);margin-top: 20px; ">
              #{{ id }} opened by
              <user-link weight="600" :user="openBy"/>
              on {{ dayjs.unix(openAt / 1000).format("YYYY-MM-DD") }} that received {{
                events.length
              }}
              comments
            </a-typography-paragraph>
          </a-space>
        </div>
      </div>
      <a-divider style="margin-top: 5px"></a-divider>
      <div class="content-container">
        <div class="left-side">

          <conversation-list :conversations="events"/>
          <a-divider/>

          <div style="display: flex; justify-content: left">
            <user-avatar :user="me"/>
            <a-card style="border-radius:10px;margin-left: 10px">
              <mavon-editor
                  style="width: 720px; height: 300px; background-color: white !important; border: none !important;"
                  language="en" placeholder="Leave a comment"
                  :subfield=false :autofocus=false v-model="cmtVal"></mavon-editor>
              <div style="display: flex;justify-content: right;margin-top: 20px">
                <a-button type="primary" status="success" shape="round" @click="revertOpenState"
                          style="margin-right: 15px" v-if="currentUserIsEditable || this.openBy===this.me">{{ isOpening ? 'Close' : 'Reopen' }}
                </a-button>
                <a-button type="primary" shape="round" @click="doComment" :disabled="!cmtVal">Comment</a-button>
              </div>
            </a-card>
          </div>
        </div>
        <div class="right-side">
          <issue-side-bar :participants="participants"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import userAvatar from "@/components/user/F1userAvatar.vue";
import issueSideBar from "@/components/issue/F1issueSideBar";
import conversationList from "@/components/conversation/F1conversationList";
import dayjs from 'dayjs';
import request from "@/utils/request";
import jsCookie from "js-cookie";
import {BACKEND_URL} from "@/utils/config";

export default {
  name: "index",
  components: {
    userLink,
    userAvatar,
    issueSideBar,
    conversationList
  },
  data() {
    return {
      id: this.$route.params.index,
      title: '',
      isOpening: true,
      openBy: '',
      openAt: 0,
      events: [],
      participants: [],

      dayjs,
      me: localStorage.getItem('inUser'),
      currentUserIsEditable: false,

      cmtVal: '',
    }
  },
  watch: {
    $route() {
      if (this.$route.name !== 'notFound') {
        this.init()
      }
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    doComment() {
      request.post(BACKEND_URL + `/core/api/issue/comment/${this.$route.params.user}/${this.$route.params.rep}/${this.id}`, {
        ctx: this.cmtVal
      }, {
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      }).then(resp => {
        this.cmtVal = ''
        this.init()
      })
    },
    init() {
      request.get(BACKEND_URL + `/core/api/repo/verify_accessable/${this.$route.params.user}/${this.$route.params.rep}`)
      request.get(BACKEND_URL + `/core/api/repo/is_collaborator/${this.$route.params.user}/${this.$route.params.rep}/?colab=${localStorage.getItem('inUser')}`)
          .then(resp => {
            this.currentUserIsEditable = resp.data.data || this.openBy == localStorage.getItem('inUser')
          })
      request.get(BACKEND_URL + `/core/api/issue/details/${this.$route.params.user}/${this.$route.params.rep}/${this.id}`)
          .then(resp => {
            this.title = resp.data.data.title
            this.isOpening = resp.data.data.isOpening
            this.openBy = resp.data.data.openBy
            this.openAt = resp.data.data.openAt
            this.events = resp.data.data.events
            this.participants = resp.data.data.participants
          })
    },
    revertOpenState() {
      request.get(BACKEND_URL + `/core/api/issue/setclose/${this.$route.params.user}/${this.$route.params.rep}/${this.id}?close=${this.isOpening}`,  {
        headers: {
          'Authorization': jsCookie.get('Authorization')
        }
      }).then(resp => {
        console.log(resp.data)
        this.init()
      })
    }
  }
}
</script>

<style scoped>
.content-container {
  display: grid;
  grid-template-columns: 6fr 2fr;
  padding-top: 20px;
}

.left-side {
  grid-column: 1/ span 1;
  padding: 10px 40px;
}

.right-side {
  width: 296px;
  grid-column: 2/ span 1;
  padding-top: 10px;
}

</style>