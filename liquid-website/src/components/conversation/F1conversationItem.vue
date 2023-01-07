<template>
  <a-timeline-item>
    <template #dot>
      <user-avatar :user="user"/>
    </template>
    <a-space align="start" style="margin-left: 10px; margin-top: -10px; margin-bottom: 30px">
      <a-card style="border-radius: 10px"
              :header-style="{backgroundColor:'var(--color-neutral-1)', borderTopLeftRadius:'10px', borderTopRightRadius:'10px'}">
        <template #title>
          <a-space>
            <user-link :user="user"/>
            <div class="simple_text" style="color: grey; font-size: 12px"> commented at
              {{ dayjs.unix(ts / 1000).format('YYYY-MM-DD HH:mm') }}
            </div>

          </a-space>
        </template>

        <template #extra>
          <a-space>
            <a-button class="button" shape="round" type="outline" size="mini"
                      style="color: dimgrey; margin-right: 5px; scale: 0.8" v-if="!!character">
              {{ character }}
            </a-button>
            <div class="two_icon_link">
              <a-dropdown @select="handleSelect" class="dropdown1">
                <a-link :hoverable="false" class="smile">
                  <template #icon>
                    <icon-face-smile-fill/>
                  </template>
                </a-link>
                <template #content>
                  <a-doption>
                    <template #icon>
                      <icon-thumb-up-fill/>
                    </template>
                  </a-doption>

                  <a-doption>
                    <template #icon>
                      <icon-thumb-down-fill/>
                    </template>
                  </a-doption>
                </template>
              </a-dropdown>

              <a-dropdown @select="handleSelect" class="dropdown2">
                <a-link :hoverable="false" class="smile">
                  <template #icon>
                    <icon-more/>
                  </template>
                </a-link>
                <template #content>
                  <a-doption>Copy link</a-doption>
                  <a-doption>Quote reply</a-doption>
                  <a-doption>Reference in new issue</a-doption>
                </template>
              </a-dropdown>
            </div>
          </a-space>
        </template>
        <div v-html="md2html(ctx)" style="min-width: 740px; margin-top: -20px; margin-bottom: -20px"/>
      </a-card>

    </a-space>
  </a-timeline-item>

</template>

<script>
import userLink from "@/components/user/F1userLink.vue";
import userAvatar from "@/components/user/F1userAvatar.vue";
import dayjs from "dayjs";
import MarkdownIt from 'markdown-it'

const mdcv = new MarkdownIt()

export default {
  name: "conversationItem",
  components: {
    userLink,
    userAvatar
  },
  props: {
    user: {default: ''},
    ts: {default: 0},
    character: {default: ''},
    ctx: {default: ''},
  },
  data() {
    return {
      dayjs,
    }
  },
  methods: {
    md2html(md) {
      return mdcv.render(md)
    }
  }
}
</script>
