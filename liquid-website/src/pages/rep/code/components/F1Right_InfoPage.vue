<template>
  <a-space direction="vertical" style="width: 100%">
    <div class="about">{{ $t('About') }}</div>
    <div class="repo_description"> {{ getDescription }}</div>
    <a-link :hoverable="false">
      <template #icon>
        <icon-book/>
      </template>
      <template #default>Readme</template>
    </a-link>
    <a-link @click="this.$router.push({name:'insight', params:{type:'star'}})" :hoverable="false">
      <template #icon>
        <icon-star/>
      </template>
      <template #default>{{ getStarCount }} {{ $t('star') }}</template>
    </a-link>
    <a-link @click="this.$router.push({name:'insight', params:{type:'watch'}})" :hoverable="false">
      <template #icon>
        <icon-eye/>
      </template>
      <template #default>{{ getWatchCount }} {{ $t('watch') }}</template>
    </a-link>
    <a-link @click="this.$router.push({name:'insight', params:{type:'fork'}})" :hoverable="false">
      <template #icon>
        <icon-branch/>
      </template>
      <template #default>{{ getForkCount }} {{ $t('folk') }}</template>
    </a-link>
    <a-divider/>
    <div class="release">
      {{ $t('Releases') }}
      <a-badge
          :count="1"
          :dotStyle="{ background: '#E5E6EB', color: '#86909C' }"
      />
    </div>
    <release_element/>
    <a-link :hoverable="false">
      <template #default>+{{ $t('more') }}</template>
    </a-link>
    <a-divider/>
    <div class="package">{{ $t('Packages') }}</div>
    <div class="package_description">No packages published</div>
    <a-divider/>
    <contributors :contributors="contributor"/>
  </a-space>
</template>

<script>
import release_element from "@/pages/rep/code/components/F1release_element";
import contributors from "@/pages/rep/code/components/F1contributors";
import request from "@/utils/request";
import {BACKEND_URL} from "@/utils/config";
import {mapGetters} from 'vuex'

export default {
  name: "Right_InfoPage",
  components: {release_element, contributors},
  data() {
    return {
      contributor: []
    }
  },
  computed: {
    ...mapGetters('rep', ['getStarCount', 'getWatchCount', 'getForkCount',
      'getPrivateRepo', 'getStarCount', 'getWatchCount', 'getForkCount', 'getDescription'])
  },
  mounted() {
    request.get(BACKEND_URL + `/core/api/repo/listcontributors/${this.$route.params.user}/${this.$route.params.rep}`)
        .then(resp => {
          this.contributor = resp.data.data
        })
  },
  methods: {}
}
</script>

<style scoped>
.about {
  font-size: 18px;
  font-family: "Arial Rounded MT Bold";
}

.release {
  font-size: 18px;
  font-family: "Arial Rounded MT Bold";
}

.repo_description {
  margin-top: 8px;
  font-size: 16px;
  font-family: "Avenir Next";
}

.package {
  font-size: 18px;
  font-family: "Arial Rounded MT Bold";
}

.package_description {
  margin-top: 8px;
  font-size: 11px;
  font-family: "Avenir Next";
}
</style>