<template>
  <div v-html="result" class="markdown-preview"></div>
</template>

<script lang="ts" setup>
import { defineProps, watchEffect } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import typescript from 'highlight.js/lib/languages/typescript'

import 'highlight.js/styles/github-dark.css'

const props = defineProps<{
  source: string
}>()

hljs.registerLanguage('vue', typescript)

const md = MarkdownIt({
  breaks: true,
  linkify: true,
  typographer: true,
  highlight: function (str: string, lang: string): string {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return (
          '<pre><code class="hljs">' +
          hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
          '</code></pre>'
        )
      } catch {}
    }
    return '<pre><code class="hljs">' + md.utils.escapeHtml(str) + '</code></pre>'
  },
})
let result = md.render(props.source)

watchEffect(() => {
  result = md.render(props.source)
})
</script>

<style scoped>
.markdown-preview {
  padding: 12px 16px;
  border: 1px solid #eee;
  border-radius: 4px;
  background-color: #f9f9f9;
}
</style>
