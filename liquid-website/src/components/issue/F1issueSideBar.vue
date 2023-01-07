<template>
  <div class="title" style="margin-top: 30px">Assignees</div>
  <div class="text"> {{ assign_description }}</div>
  <a-divider class="divider"/>

  <div class="title">Projects</div>
  <div class="text"> {{ project_description }}</div>
  <a-divider class="divider"/>

  <div class="title">Milestone</div>
  <div class="text"> {{ milestone_description }}</div>
  <a-divider class="divider"/>

  <div class="title">Development</div>
  <div class="text"> {{ develop_description }}</div>
  <a-divider class="divider"/>

  <div class="container1">
    <div class="title">Notifications</div>
    <div>
      <a-link href="link" :hoverable="false" class="text1">customize</a-link>
    </div>
  </div>
  <a-button type="outline" shape="round" long class="button">
    <icon-notification/>
    Subscribe
  </a-button>
  <div class="text1"> {{ notify_description }}</div>
  <a-divider class="divider"/>

  <div class="title"> {{ participants.length }} {{ participants.length > 1 ? 'participants' : 'participant' }}</div>
  <div style="padding-top: 8px; display: flex; flex-direction: row">
    <div v-for="item in participants" style="margin-right: 4px; scale: .8">
      <user-avatar :user="item"/>
    </div>
  </div>
</template>

<script>
import userAvatar from "@/components/user/F1userAvatar.vue";

export default {
  name: "issueSideBar",
  components: {
    userAvatar
  },
  props: {
    participants: {default: []}
  },
  data() {
    return {
      assign_description: "No one assigned",
      label_description: "None yet",
      project_description: "None yet",
      milestone_description: "No milestone",
      develop_description: "No branches or pull requests",
      notify_description: "You’re not receiving notifications from this thread."
    }
  }
}
</script>

<style scoped>
.title {
  font-family: "Arial Rounded MT Bold";
  font-size: 13px;
}

.text {
  font-size: 11px;
  margin-top: 11px;
}

.text1 {
  font-size: 11px;
  color: dimgrey;
}

.divider {
  margin-top: 17px;
  margin-bottom: 15px;
}

.container1 {
  display: flex;
  justify-content: space-between;
  width: 290px;
}

.button {
  margin-top: 5px;
  margin-bottom: 5px;
}

.avatar {
  margin-top: 8px;
}

</style>