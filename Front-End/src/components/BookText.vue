<template>
  <div class="book-text-container" @mouseup="onMouseUp">
    <div class="book-text" ref="textRef">
      <template v-for="(block, idx) in blocks" :key="idx">
        <span v-if="block.type === 'text'">{{ block.text }}</span>
        <span
          v-else
          class="annotated-text"
          @mouseenter="showPopover(idx)"
          @mouseleave="hidePopover"
          @click="showPopover(idx)"
        >
          <a href="javascript:void(0)"><u>{{ block.text }}</u></a>
        </span>
      </template>
    </div>
    <div v-if="showAddBtn" :style="addBtnPos" class="add-annotate-btn">
      <button @click="showInput = true; showAddBtn = false">添加批注</button>
    </div>
    <div v-if="showInput" :style="addBtnPos" class="note-input-float">
      <input v-model="newNote" placeholder="请输入你的见解..." @keydown.enter="submitNote" />
      <button @click="submitNote">添加</button>
    </div>
    <div v-if="popoverIdx !== null" :style="popoverPos" class="note-popover">
      <div class="note-list">
        <div v-for="(view, vIdx) in blocks[popoverIdx].views" :key="vIdx" class="note-view">{{ view }}</div>
      </div>
      <div class="note-input">
        <input v-model="newView" placeholder="发表见解..." @keydown.enter="addView(popoverIdx)" />
        <button @click="addView(popoverIdx)">发表</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
const textRef = ref(null)
const rawText = `有一句成语叫胆小如鼠，说的就是我的故事。这是我的老师告诉我的，当时我还在读小学，我记得是在秋天的一节语文课上，我们的老师站在讲台上，他穿着藏青的卡其布中山服，里面还有一件干净的白衬衣。那时候我坐在第一排座位的中间，我仰脸看着他，他手里拿着一册课本，手指上布满了红的、白的和黄色的粉笔灰，他正在朗读着课文，他的脸和他的手还有他手上的课本都对我居高临下，于是他的唾沫就不停地喷到我的脸上，我只好不停地抬起自己的手，不停地去擦掉他的唾沫。他注意到自己的唾沫正在喷到我的脸上，而当他的唾沫飞过来的那一刻，我就会害怕地把一只眼睛闭上。他停止了朗读，放下了课本，他的身体绕过了讲台，来到我的面前，他伸过来那只布满粉笔灰的右手，像是给我洗脸似的在我脸上抹了一把，然后他转身拿起放在讲台上的课本，在教室里走动着清朗地读起来。唾液干净了我脸上的唾沫，抹让我脸沾满了红的、白的和黄色的粉笔灰，我听到了教室里响起嗡嗡、哗哗、哈哈的笑声，因为我的脸像一只蝴蝶那样花哨了。`;

// blocks: [{type: 'text', text: 'xxx'}, {type: 'annotate', text: 'xxx', views: []}]
const blocks = ref([{ type: 'text', text: rawText }])
const showAddBtn = ref(false)
const addBtnPos = reactive({ left: '0px', top: '0px' })
const showInput = ref(false)
const newNote = ref('')
const selectionRange = ref(null)
const popoverIdx = ref(null)
const popoverPos = reactive({ left: '0px', top: '0px' })
const newView = ref('')

function onMouseUp(e) {
  const sel = window.getSelection()
  if (!sel || sel.isCollapsed) {
    showAddBtn.value = false
    return
  }
  const selectedText = sel.toString()
  if (!selectedText.trim()) {
    showAddBtn.value = false
    return
  }
  // 计算按钮位置
  const range = sel.getRangeAt(0)
  const rect = range.getBoundingClientRect()
  const parentRect = textRef.value.getBoundingClientRect()
  addBtnPos.left = `${rect.left - parentRect.left + rect.width / 2 - 40}px`
  addBtnPos.top = `${rect.top - parentRect.top - 40}px`
  showAddBtn.value = true
  showInput.value = false
  selectionRange.value = { start: rawText.indexOf(selectedText), end: rawText.indexOf(selectedText) + selectedText.length, text: selectedText }
}

function submitNote() {
  if (!newNote.value.trim() || !selectionRange.value) return
  // 拆分 blocks
  const { start, end, text } = selectionRange.value
  const before = blocks.value[0].text.slice(0, start)
  const selected = blocks.value[0].text.slice(start, end)
  const after = blocks.value[0].text.slice(end)
  const newBlocks = []
  if (before) newBlocks.push({ type: 'text', text: before })
  newBlocks.push({ type: 'annotate', text: selected, views: [newNote.value] })
  if (after) newBlocks.push({ type: 'text', text: after })
  blocks.value = newBlocks
  showInput.value = false
  showAddBtn.value = false
  newNote.value = ''
  selectionRange.value = null
}

function showPopover(idx) {
  popoverIdx.value = idx
  nextTick(() => {
    // 计算 popover 位置
    const el = textRef.value.childNodes[idx]
    if (el) {
      const rect = el.getBoundingClientRect()
      const parentRect = textRef.value.getBoundingClientRect()
      popoverPos.left = `${rect.left - parentRect.left}px`
      popoverPos.top = `${rect.bottom - parentRect.top + 8}px`
    }
  })
}
function hidePopover() {
  popoverIdx.value = null
}
function addView(idx) {
  if (!newView.value.trim()) return
  blocks.value[idx].views.push(newView.value)
  newView.value = ''
}
</script>

<style scoped>
.book-text-container {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 400px;
  background: #222;
  overflow: auto;
  padding: 2rem;
  font-size: 1.1rem;
  line-height: 2;
  color: #e3e6eb;
}
.book-text {
  user-select: text;
  word-break: break-all;
  white-space: pre-wrap;
}
.annotated-text {
  color: #2563eb;
  cursor: pointer;
  text-decoration: underline;
  font-weight: bold;
}
.add-annotate-btn {
  position: absolute;
  z-index: 20;
}
.add-annotate-btn button {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 0.25rem 0.75rem;
  cursor: pointer;
  font-size: 1rem;
}
.note-input-float {
  position: absolute;
  z-index: 100;
  background: #23232a;
  color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.2);
  padding: 0.75rem 1rem;
  display: flex;
  gap: 0.5rem;
  align-items: center;
}
.note-input-float input {
  flex: 1;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #23232a;
  background: #18181a;
  color: #fff;
}
.note-input-float button {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 0.25rem 0.75rem;
  cursor: pointer;
}
.note-popover {
  position: absolute;
  min-width: 200px;
  max-width: 300px;
  background: #23232a;
  color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.2);
  padding: 1rem;
  z-index: 100;
}
.note-list {
  margin-bottom: 0.5rem;
}
.note-view {
  padding: 0.25rem 0;
  border-bottom: 1px solid #3332;
  font-size: 0.95rem;
}
.note-input {
  display: flex;
  gap: 0.5rem;
}
.note-input input {
  flex: 1;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #23232a;
  background: #18181a;
  color: #fff;
}
.note-input button {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 0.25rem 0.75rem;
  cursor: pointer;
}
</style> 