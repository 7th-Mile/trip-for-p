<!-- MarkdownViewer.vue -->
<template>
    <div class="markdown-viewer" v-html="renderedContent"></div>
</template>

<script setup>
import { computed, defineProps } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

// props 정의
const props = defineProps({
    content: {
        type: String,
        required: true,
        default: ''
    }
})

// marked 옵션 설정
marked.setOptions({
    gfm: true,
    breaks: true,
    pedantic: false,
    smartLists: true,
    smartypants: true
})

// 마크다운을 HTML로 변환하고 sanitize 처리
const renderedContent = computed(() => {
    const rawHtml = marked.parse(props.content)
    return DOMPurify.sanitize(rawHtml)
})
</script>

<style scoped>
.markdown-viewer {
    max-width: 100%;
    line-height: 1.6;
}

.markdown-viewer :deep(h1) {
    font-size: 2em;
    margin-bottom: 0.5em;
    font-weight: bold;
}

.markdown-viewer :deep(h2) {
    font-size: 1.5em;
    margin-bottom: 0.5em;
    font-weight: bold;
}

.markdown-viewer :deep(h3) {
    font-size: 1.2em;
    margin-bottom: 0.5em;
    font-weight: bold;
}

.markdown-viewer :deep(p) {
    margin-bottom: 1em;
}

.markdown-viewer :deep(ul),
.markdown-viewer :deep(ol) {
    margin-bottom: 1em;
    padding-left: 2em;
}

.markdown-viewer :deep(ul) {
    list-style-type: disc;
    margin-bottom: 1em;
    padding-left: 2em;
}

.markdown-viewer :deep(li) {
    margin-bottom: 0.5em;
    display: list-item;
    list-style: inherit;
}

.markdown-viewer :deep(li p) {
    margin: 0;
    display: inline;
}

.markdown-viewer :deep(code) {
    background-color: #f5f5f5;
    padding: 0.2em 0.4em;
    border-radius: 3px;
    font-family: monospace;
}

.markdown-viewer :deep(pre) {
    background-color: #f5f5f5;
    padding: 1em;
    border-radius: 5px;
    overflow-x: auto;
    margin-bottom: 1em;
}

.markdown-viewer :deep(pre code) {
    background-color: transparent;
    padding: 0;
}

.markdown-viewer :deep(blockquote) {
    border-left: 4px solid #ddd;
    padding-left: 1em;
    margin-bottom: 1em;
    color: #666;
}

.markdown-viewer :deep(a) {
    color: #0066cc;
    text-decoration: none;
}

.markdown-viewer :deep(a:hover) {
    text-decoration: underline;
}

.markdown-viewer :deep(table) {
    border-collapse: collapse;
    width: 100%;
    margin-bottom: 1em;
}

.markdown-viewer :deep(th),
.markdown-viewer :deep(td) {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
}

.markdown-viewer :deep(th) {
    background-color: #f5f5f5;
}
</style>
